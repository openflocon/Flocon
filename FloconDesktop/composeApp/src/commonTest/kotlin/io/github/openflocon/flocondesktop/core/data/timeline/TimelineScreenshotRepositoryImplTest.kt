package io.github.openflocon.flocondesktop.core.data.timeline

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimelineScreenshotRepositoryImplTest {

    private val root: File = Files.createTempDirectory("flocon-screens").toFile().also { it.deleteOnExit() }
    private val deviceA = DeviceIdAndPackageNameDomainModel(deviceId = "a", packageName = "com.app", appInstance = 1L)
    private val deviceB = DeviceIdAndPackageNameDomainModel(deviceId = "b", packageName = "com.app", appInstance = 1L)

    private fun repository(max: Int = 300) = TimelineScreenshotRepositoryImpl(rootDir = root, maxPerDevice = max)

    // creates the file like the recorder does, then registers it
    private suspend fun TimelineScreenshotRepositoryImpl.addShot(target: DeviceIdAndPackageNameDomainModel, timestamp: Long): File {
        val file = newJpegFile(target, timestamp).also { it.writeBytes(byteArrayOf(1)) }
        add(TimelineScreenshotDomainModel(timestamp, file.absolutePath, target.deviceId, target.packageName))
        return file
    }

    @Test
    fun `added screenshots are observed oldest first per device`() = runBlocking {
        val repository = repository()
        repository.addShot(deviceA, 20)
        repository.addShot(deviceA, 10)

        assertEquals(listOf(10L, 20L), repository.observe(deviceA).first().map { it.timestamp })
    }

    @Test
    fun `the oldest files are deleted from disk and from the index past the cap`() = runBlocking {
        val repository = repository(max = 3)
        val files = (1L..5L).map { repository.addShot(deviceA, it) }

        assertEquals(listOf(3L, 4L, 5L), repository.observe(deviceA).first().map { it.timestamp })
        assertFalse(files[0].exists())
        assertFalse(files[1].exists())
        assertTrue(files[2].exists())
    }

    @Test
    fun `a screenshot whose file was deleted from disk is dropped from the index on the next add`() = runBlocking {
        val repository = repository()
        val gone = repository.addShot(deviceA, 1)
        gone.delete()

        repository.addShot(deviceA, 2)

        assertEquals(listOf(2L), repository.observe(deviceA).first().map { it.timestamp })
    }

    @Test
    fun `purgeAll deletes the directory and empties the index`() = runBlocking {
        val repository = repository()
        val file = repository.addShot(deviceA, 1)

        repository.purgeAll()

        assertFalse(file.exists())
        assertFalse(root.exists())
        assertEquals(emptyList(), repository.observe(deviceA).first())
    }

    @Test
    fun `two devices do not see each other's screenshots`() = runBlocking {
        val repository = repository()
        repository.addShot(deviceA, 1)
        repository.addShot(deviceB, 2)

        assertEquals(listOf(1L), repository.observe(deviceA).first().map { it.timestamp })
        assertEquals(listOf(2L), repository.observe(deviceB).first().map { it.timestamp })
    }
}
