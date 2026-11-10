package io.github.openflocon.domain.maintenance.repository

import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import kotlinx.coroutines.flow.Flow

interface DatabaseMaintenanceRepository {
    /** Size on disk of the Flocon database, in bytes. */
    suspend fun getDatabaseSizeBytes(): Long

    /** Deletes the captured data older than [beforeTimestampMs], then compacts the database. */
    suspend fun purgeDataBefore(beforeTimestampMs: Long)

    /** The age above which the data is deleted at each launch of the app. */
    val autoPurgeInterval: Flow<AutoPurgeInterval>

    suspend fun setAutoPurgeInterval(interval: AutoPurgeInterval)
}
