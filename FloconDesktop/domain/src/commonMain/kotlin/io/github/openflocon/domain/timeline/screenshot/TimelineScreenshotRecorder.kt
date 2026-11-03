package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull

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
                // the failures of the previous plan (another device, the feature turned off) are not the ones of this one
                consecutiveFailures = 0
                _isFailing.value = false
                if (plan != null) captureEvery(plan)
            }
    }

    // a full interval of rest after each capture: a slow capture (wireless adb) never keeps the device busy all the time
    private suspend fun captureEvery(plan: Plan) {
        while (true) {
            captureOnce(plan.device)
            delay(plan.intervalMs)
        }
    }

    private suspend fun captureOnce(target: DeviceIdAndPackageNameDomainModel) {
        val timestamp = System.currentTimeMillis()
        val file = repository.newJpegFile(target, timestamp)
        // a capture that hangs counts as a failure, and the loop goes on
        val result = withTimeoutOrNull(CAPTURE_TIMEOUT_MS) { capturer.capture(target, file) }
        (result ?: Failure(IllegalStateException("Screenshot timeout"))).fold(
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
        const val CAPTURE_TIMEOUT_MS = 30_000L
    }
}
