package io.github.openflocon.flocondesktop.common.db

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

/**
 * The screenshots and the video recordings live in the `Screenshot` and `ScreenRecord` folders of the Flocon documents
 * folder. Only the files directly inside them are touched, never the folders themselves nor anything else the user
 * may keep in the documents folder.
 *
 * - Parameter documentsFolder: the Flocon documents folder, asked each time as the user can change it in the settings.
 */
internal class MediaFiles(private val documentsFolder: () -> File) {

    fun sizeBytes(): Long = files().sumOf { it.length() }

    fun deleteModifiedBefore(beforeTimestampMs: Long) {
        files()
            .filter { it.lastModified() < beforeTimestampMs }
            .forEach { it.delete() }
    }

    private fun files(): List<File> = FOLDERS
        .flatMap { name -> File(documentsFolder(), name).listFiles().orEmpty().asList() }
        // a link is left alone: it may point outside the documents folder
        .filter { it.isFile && !Files.isSymbolicLink(it.toPath()) }

    private companion object {
        val FOLDERS = listOf("Screenshot", "ScreenRecord")
    }
}

internal class MediaFilesRepositoryImpl(
    private val mediaFiles: MediaFiles,
    private val dispatcherProvider: DispatcherProvider,
    private val settingsDataSource: SettingsDataSource,
) : MediaFilesRepository {

    override suspend fun getSizeBytes(): Long = withContext(dispatcherProvider.data) {
        mediaFiles.sizeBytes()
    }

    override suspend fun deleteFilesBefore(beforeTimestampMs: Long): Unit = withContext(dispatcherProvider.data) {
        mediaFiles.deleteModifiedBefore(beforeTimestampMs)
    }

    override val autoCleanupInterval: Flow<AutoPurgeInterval> = settingsDataSource.autoMediaCleanupDays
        // these are files the user saved on purpose: nothing is deleted unless asked
        .map { AutoPurgeInterval.fromDays(it, default = AutoPurgeInterval.Never) }

    override suspend fun setAutoCleanupInterval(interval: AutoPurgeInterval) {
        settingsDataSource.setAutoMediaCleanupDays(interval.days)
    }
}
