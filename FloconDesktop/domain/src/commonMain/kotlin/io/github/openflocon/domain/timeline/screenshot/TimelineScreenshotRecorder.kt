package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Takes a screenshot of the current app every interval, while the feature is on and the device is connected.
 * [run] never returns: cancel it to stop.
 */
class TimelineScreenshotRecorder(
    private val timelinePreferencesRepository: TimelinePreferencesRepository,
    private val currentDevice: Flow<DeviceIdAndPackageNameDomainModel?>,
    private val isCurrentDeviceLive: Flow<Boolean>,
    private val capturer: TimelineScreenCapturer,
    private val repository: TimelineScreenshotRepository,
) {

    private val _isFailing = MutableStateFlow(false)

    // the last captures failed (see [FAILURES_BEFORE_WARNING]): adb is not available, the device is locked…
    val isFailing: StateFlow<Boolean> = _isFailing

    private var consecutiveFailures = 0

    private data class Plan(val device: DeviceIdAndPackageNameDomainModel, val intervalMs: Long)

    suspend fun run() {
        val settings = timelinePreferencesRepository.settings
            .map { it.screenshots to it.screenshotIntervalSeconds }
            .distinctUntilChanged()

        combine(settings, currentDevice, isCurrentDeviceLive) { (enabled, seconds), device, live ->
            if (enabled && live && device != null) Plan(device, seconds * 1_000L) else null
        }
            .distinctUntilChanged()
            // a new plan (interval, device, switch) cancels the loop in progress and starts at once
            .collectLatest { plan ->
                if (plan != null) captureEvery(plan)
            }
    }

    // a tick that arrives while a capture is running is merged with the next one (conflate): a slow capture skips ticks
    private suspend fun captureEvery(plan: Plan) {
        flow {
            while (true) {
                emit(Unit)
                delay(plan.intervalMs)
            }
        }
            .conflate()
            .collect { captureOnce(plan.device) }
    }

    private suspend fun captureOnce(target: DeviceIdAndPackageNameDomainModel) {
        val timestamp = System.currentTimeMillis()
        val file = repository.newJpegFile(target, timestamp)
        capturer.capture(target, file).fold(
            doOnFailure = {
                consecutiveFailures++
                if (consecutiveFailures >= FAILURES_BEFORE_WARNING) _isFailing.value = true
            },
            doOnSuccess = {
                consecutiveFailures = 0
                _isFailing.value = false
                // stored for the device the capture was started for, even if the selection changed meanwhile
                repository.add(
                    TimelineScreenshotDomainModel(
                        timestamp = timestamp,
                        filePath = file.absolutePath,
                        deviceId = target.deviceId,
                        packageName = target.packageName,
                    ),
                )
            },
        )
    }

    private companion object {
        const val FAILURES_BEFORE_WARNING = 3
    }
}
