package io.github.openflocon.domain.maintenance.repository

interface DatabaseMaintenanceRepository {
    /** Size on disk of the Flocon database, in bytes. */
    suspend fun getDatabaseSizeBytes(): Long

    /** Deletes the captured data older than [beforeTimestampMs], then compacts the database. */
    suspend fun purgeDataBefore(beforeTimestampMs: Long)
}
