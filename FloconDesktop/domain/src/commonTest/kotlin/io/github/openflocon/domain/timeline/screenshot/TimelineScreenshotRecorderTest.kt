package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.repository.TimelinePreferencesRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TimelineScreenshotRecorderTest {

    private val deviceA = DeviceIdAndPackageNameDomainModel(deviceId = "a", packageName = "com.app", appInstance = 1L)
    private val deviceB = DeviceIdAndPackageNameDomainModel(deviceId = "b", packageName = "com.app", appInstance = 1L)

    private class FakePreferences : TimelinePreferencesRepository {
        val state = MutableStateFlow(TimelineSettingsDomainModel(screenshots = true, screenshotIntervalSeconds = 10))
        override val settings: Flow<TimelineSettingsDomainModel> = state
        override suspend fun setAutoScroll(autoScroll: Boolean) = Unit
        override suspend fun setInvertList(invertList: Boolean) = Unit
        override suspend fun setPinPanel(pinPanel: Boolean) = Unit
        override suspend fun setGraph(graph: Boolean) = Unit
        override suspend fun setSelectedSources(sources: Set<TimelineSource>) = Unit
        override suspend fun setScreenshots(screenshots: Boolean) {
            state.value = state.value.copy(screenshots = screenshots)
        }
        override suspend fun setScreenshotIntervalSeconds(seconds: Int) {
            state.value = state.value.copy(screenshotIntervalSeconds = seconds)
        }
    }

    private class FakeRepository : TimelineScreenshotRepository {
        val added = mutableListOf<TimelineScreenshotDomainModel>()
        override fun observe(target: DeviceIdAndPackageNameDomainModel) = MutableStateFlow(added.toList())
        override fun newJpegFile(target: DeviceIdAndPackageNameDomainModel, timestamp: Long) = File("${target.deviceId}_$timestamp.jpg")
        override suspend fun add(screenshot: TimelineScreenshotDomainModel) {
            added += screenshot
        }
        override suspend fun purgeAll() = Unit
    }

    private class FakeCapturer : TimelineScreenCapturer {
        val calls = mutableListOf<DeviceIdAndPackageNameDomainModel>()
        var running = 0
        var maxRunning = 0
        var durationMs = 0L
        var gate: CompletableDeferred<Unit>? = null
        var result: Either<Throwable, Unit> = Success(Unit)

        override suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit> {
            calls += target
            running++
            maxRunning = maxOf(maxRunning, running)
            try {
                gate?.await()
                if (durationMs > 0) delay(durationMs)
            } finally {
                running--
            }
            return result
        }
    }

    private class Env(
        val preferences: FakePreferences = FakePreferences(),
        val repository: FakeRepository = FakeRepository(),
        val capturer: FakeCapturer = FakeCapturer(),
        val device: MutableStateFlow<DeviceIdAndPackageNameDomainModel?>,
        val live: MutableStateFlow<Boolean> = MutableStateFlow(true),
    ) {
        val recorder = TimelineScreenshotRecorder(
            timelinePreferencesRepository = preferences,
            currentDevice = device,
            isCurrentDeviceLive = live,
            capturer = capturer,
            repository = repository,
        )
    }

    private fun env() = Env(device = MutableStateFlow(deviceA))

    private fun TestScope.start(env: Env) = backgroundScope.launch { env.recorder.run() }

    @Test
    fun `nothing is captured while the feature is off`() = runTest {
        val env = env().also { it.preferences.state.value = it.preferences.state.value.copy(screenshots = false) }
        start(env)

        advanceTimeBy(60_000)

        assertEquals(0, env.capturer.calls.size)
    }

    @Test
    fun `nothing is captured while the device is not live`() = runTest {
        val env = env().also { it.live.value = false }
        start(env)

        advanceTimeBy(60_000)

        assertEquals(0, env.capturer.calls.size)
    }

    @Test
    fun `a capture is taken right away then every interval`() = runTest {
        val env = env()
        start(env)

        runCurrent()
        assertEquals(1, env.capturer.calls.size)
        advanceTimeBy(10_000)
        runCurrent()
        assertEquals(2, env.capturer.calls.size)
        advanceTimeBy(10_000)
        runCurrent()
        assertEquals(3, env.capturer.calls.size)
        assertEquals(3, env.repository.added.size)
    }

    @Test
    fun `changing the interval restarts the loop with the new one immediately`() = runTest {
        val env = env()
        start(env)
        runCurrent()
        assertEquals(1, env.capturer.calls.size)

        env.preferences.setScreenshotIntervalSeconds(5)
        runCurrent()
        // the loop restarted: a capture right away, then every 5 s
        assertEquals(2, env.capturer.calls.size)
        advanceTimeBy(5_000)
        runCurrent()
        assertEquals(3, env.capturer.calls.size)
    }

    @Test
    fun `turning the feature off stops the loop and keeps stored screenshots`() = runTest {
        val env = env()
        start(env)
        runCurrent()
        assertEquals(1, env.repository.added.size)

        env.preferences.setScreenshots(false)
        advanceTimeBy(60_000)
        runCurrent()

        assertEquals(1, env.capturer.calls.size)
        assertEquals(1, env.repository.added.size)
    }

    @Test
    fun `a capture slower than the interval skips ticks instead of queueing`() = runTest {
        val env = env().also { it.capturer.durationMs = 25_000 }
        start(env)

        advanceTimeBy(100_000)
        runCurrent()

        // 100 s with a 25 s capture and a 10 s interval: never two at once, and far fewer than the 10 ticks
        assertEquals(1, env.capturer.maxRunning)
        assertTrue(env.capturer.calls.size <= 5, "calls: ${env.capturer.calls.size}")
    }

    @Test
    fun `a capture started for device A that finishes after switching to device B is stored under A`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val env = env().also { it.capturer.gate = gate }
        start(env)
        runCurrent()
        assertEquals(listOf(deviceA), env.capturer.calls)

        env.device.value = deviceB
        runCurrent()
        // the loop restarted for B, and the capture of A is cancelled or finishes: A's result is never stored for B
        gate.complete(Unit)
        runCurrent()

        // whatever is stored, its file and its device are the ones the capture was started for
        assertTrue(env.repository.added.all { File(it.filePath).name.startsWith("${it.deviceId}_") })
    }

    @Test
    fun `isFailing turns true after 3 failures in a row and back to false after a success`() = runTest {
        val env = env().also { it.capturer.result = Failure(RuntimeException("adb")) }
        start(env)

        runCurrent()
        advanceTimeBy(10_000)
        runCurrent()
        assertFalse(env.recorder.isFailing.value)
        advanceTimeBy(10_000)
        runCurrent()
        assertTrue(env.recorder.isFailing.value)

        env.capturer.result = Success(Unit)
        advanceTimeBy(10_000)
        runCurrent()
        assertFalse(env.recorder.isFailing.value)
    }

    @Test
    fun `a failed capture does not stop the loop and stores nothing`() = runTest {
        val env = env().also { it.capturer.result = Failure(RuntimeException("adb")) }
        start(env)

        runCurrent()
        advanceTimeBy(10_000)
        runCurrent()

        assertEquals(2, env.capturer.calls.size)
        assertEquals(0, env.repository.added.size)
    }
}
