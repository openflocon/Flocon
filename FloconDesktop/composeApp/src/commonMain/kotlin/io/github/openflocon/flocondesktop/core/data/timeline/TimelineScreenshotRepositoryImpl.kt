package io.github.openflocon.flocondesktop.core.data.timeline

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/**
 * The screenshots are files of [rootDir] (the temporary folder of the system), indexed in memory: nothing is kept
 * from one launch to the next, [purgeAll] is called at the start of the app.
 */
internal class TimelineScreenshotRepositoryImpl(
    private val rootDir: File = File(System.getProperty("java.io.tmpdir"), "flocon-timeline-screenshots"),
    private val maxPerDevice: Int = DEFAULT_MAX_PER_DEVICE,
) : TimelineScreenshotRepository {

    // by device and app, oldest first
    private val index = MutableStateFlow<Map<String, List<TimelineScreenshotDomainModel>>>(emptyMap())
    private val mutex = Mutex()

    override fun observe(target: DeviceIdAndPackageNameDomainModel): Flow<List<TimelineScreenshotDomainModel>> {
        val key = key(target.deviceId, target.packageName)
        return index.map { it[key].orEmpty() }
    }

    override fun newJpegFile(target: DeviceIdAndPackageNameDomainModel, timestamp: Long): File {
        val folder = File(rootDir, safeName(target.deviceId)).also { it.mkdirs() }
        return File(folder, "${safeName(target.packageName)}_$timestamp.jpg")
    }

    override suspend fun add(screenshot: TimelineScreenshotDomainModel) {
        mutex.withLock {
            val key = key(screenshot.deviceId, screenshot.packageName)
            // the ones deleted behind our back (temporary folder cleaned by the system) are forgotten
            val kept = (index.value[key].orEmpty() + screenshot)
                .filter { File(it.filePath).exists() }
                .sortedBy { it.timestamp }
            val overflow = (kept.size - maxPerDevice).coerceAtLeast(0)
            kept.take(overflow).forEach { File(it.filePath).delete() }
            index.value = index.value + (key to kept.drop(overflow))
        }
    }

    override suspend fun purgeAll() {
        mutex.withLock {
            rootDir.deleteRecursively()
            index.value = emptyMap()
        }
    }

    private fun key(deviceId: String, packageName: String) = "$deviceId/$packageName"

    // a device id can hold characters a file name does not accept (ex: the colon of a network address)
    private fun safeName(name: String) = name.replace(Regex("[^A-Za-z0-9._-]"), "_")

    private companion object {
        const val DEFAULT_MAX_PER_DEVICE = 300
    }
}
