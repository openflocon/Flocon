package io.github.openflocon.domain.maintenance.repository

import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import kotlinx.coroutines.flow.Flow

/**
 * The screenshots and the video recordings saved by the user in the Flocon documents folder.
 * They are plain files, not part of the database.
 */
interface MediaFilesRepository {
    /** Total size of the saved screenshots and recordings, in bytes. */
    suspend fun getSizeBytes(): Long

    /** Deletes the screenshots and recordings last modified before [beforeTimestampMs] (epoch milliseconds). */
    suspend fun deleteFilesBefore(beforeTimestampMs: Long)

    /** The age above which the files are deleted at each launch of the app. */
    val autoCleanupInterval: Flow<AutoPurgeInterval>

    suspend fun setAutoCleanupInterval(interval: AutoPurgeInterval)
}
