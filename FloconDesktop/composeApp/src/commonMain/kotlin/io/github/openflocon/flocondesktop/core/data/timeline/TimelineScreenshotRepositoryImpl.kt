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
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFilePermissions

/**
 * The screenshots are files of [rootDir] (the temporary folder of the system), indexed in memory: nothing is kept
 * from one launch to the next, [purgeAll] is called at the start of the app.
 */
internal class TimelineScreenshotRepositoryImpl(
    private val rootDir: File = File(System.getProperty("java.io.tmpdir"), "flocon-timeline-screenshots-${safeName(System.getProperty("user.name").orEmpty())}"),
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
        createRootDir()
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
            deleteWithoutFollowingLinks(rootDir)
            index.value = emptyMap()
        }
    }

    private fun key(deviceId: String, packageName: String) = "$deviceId/$packageName"

    // only its owner reads the screenshots of the app: the temporary folder of the system is shared by all the users on Linux
    private fun createRootDir() {
        val path = rootDir.toPath()
        if (Files.exists(path)) return
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            Files.createDirectories(path.parent)
            Files.createDirectory(path, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")))
        } else {
            rootDir.mkdirs()
        }
    }

    // the folder may be a link (left by someone else in the shared temporary folder): the link is removed, not its target
    private fun deleteWithoutFollowingLinks(root: File) {
        val path = root.toPath()
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) return
        Files.walkFileTree(
            path,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }

                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }
            },
        )
    }

    private companion object {
        const val DEFAULT_MAX_PER_DEVICE = 300

        // a device id can hold characters a file name does not accept (ex: the colon of a network address)
        // and a name made of dots would leave the folder
        fun safeName(name: String): String {
            val cleaned = name.replace(Regex("[^A-Za-z0-9._-]"), "_")
            return if (cleaned.all { it == '.' }) "_$cleaned" else cleaned
        }
    }
}
