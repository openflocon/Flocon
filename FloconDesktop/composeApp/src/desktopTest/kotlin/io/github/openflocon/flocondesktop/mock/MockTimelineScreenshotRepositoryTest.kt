package io.github.openflocon.flocondesktop.mock

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockTimelineScreenshotRepositoryTest {

    private val dir = File(System.getProperty("java.io.tmpdir"), "flocon-mock-screenshots-test-${System.nanoTime()}")

    @Test
    fun `the screenshots are drawn, oldest first, before the launch`() = runBlocking<Unit> {
        val repository = MockTimelineScreenshotRepository(rootDir = dir, launchedAt = 1_000_000L)
        val shots = repository.observe(DeviceIdAndPackageNameDomainModel(deviceId = "device", packageName = "com.app", appInstance = 1L)).first()

        assertTrue(shots.size > 1)
        assertEquals(shots.sortedBy { it.timestamp }, shots)
        assertEquals(1_000_000L, shots.last().timestamp)
        assertTrue(shots.all { File(it.filePath).length() > 0 })
        dir.deleteRecursively()
    }
}
