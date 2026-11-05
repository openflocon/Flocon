package io.github.openflocon.flocondesktop.common.db

import androidx.room.useWriterConnection
import io.github.openflocon.data.local.maintenance.dao.DatabaseMaintenanceDao
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import kotlinx.coroutines.withContext
import java.io.File

class DatabaseMaintenanceRepositoryImpl(
    private val database: AppDatabase,
    private val dao: DatabaseMaintenanceDao,
    private val dispatcherProvider: DispatcherProvider,
) : DatabaseMaintenanceRepository {

    override suspend fun getDatabaseSizeBytes(): Long = withContext(dispatcherProvider.data) {
        val db = getDatabaseFile()
        // in WAL mode, part of the data lives in the -wal file until the next checkpoint
        listOf(db, File(db.path + "-wal"), File(db.path + "-shm"))
            .filter { it.exists() }
            .sumOf { it.length() }
    }

    override suspend fun purgeDataBefore(beforeTimestampMs: Long): Unit = withContext(dispatcherProvider.data) {
        // the deletes go through the dao so the observed queries are invalidated
        dao.purgeNetworkCalls(beforeTimestampMs)
        dao.purgeAnalytics(beforeTimestampMs)
        dao.purgeImages(beforeTimestampMs)
        dao.purgeAccessibilityEvents(beforeTimestampMs)
        dao.purgeWebViewEvents(beforeTimestampMs)
        dao.purgeCrashReports(beforeTimestampMs)
        dao.purgeDatabaseQueryLogs(beforeTimestampMs)
        dao.purgeTableItems(beforeTimestampMs)
        dao.purgeTimelineLogs(beforeTimestampMs)

        // deleted rows keep their space in the file until the database is compacted
        database.useWriterConnection { transactor ->
            transactor.usePrepared("PRAGMA wal_checkpoint(TRUNCATE)") { it.step() }
            transactor.usePrepared("VACUUM") { it.step() }
        }
    }
}
