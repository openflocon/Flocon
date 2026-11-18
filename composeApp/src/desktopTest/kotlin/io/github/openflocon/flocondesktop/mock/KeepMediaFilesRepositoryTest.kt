package io.github.openflocon.flocondesktop.mock

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import io.github.openflocon.domain.models.maintenance.MediaFilesSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeepMediaFilesRepositoryTest {

    private val real = RecordingMediaFilesRepository()
    private val repository = KeepMediaFilesRepository(real)

    @Test
    fun `never deletes the real screenshots and recordings`() = runBlocking<Unit> {
        repository.deleteFilesBefore(System.currentTimeMillis())

        assertTrue(real.deletions.isEmpty())
    }

    @Test
    fun `still reads the real sizes and settings`() = runBlocking<Unit> {
        assertEquals(MediaFilesSize(screenshotsBytes = 10, recordingsBytes = 20), repository.getSizes())
        repository.setAutoCleanupInterval(AutoPurgeInterval.entries.last())
        assertEquals(AutoPurgeInterval.entries.last(), repository.autoCleanupInterval.first())
    }
}

private class RecordingMediaFilesRepository : MediaFilesRepository {
    val deletions = mutableListOf<Long>()
    private val interval = MutableStateFlow(AutoPurgeInterval.Never)

    override suspend fun getSizes() = MediaFilesSize(screenshotsBytes = 10, recordingsBytes = 20)

    override suspend fun deleteFilesBefore(beforeTimestampMs: Long) {
        deletions += beforeTimestampMs
    }

    override val autoCleanupInterval: Flow<AutoPurgeInterval> = interval

    override suspend fun setAutoCleanupInterval(interval: AutoPurgeInterval) {
        this.interval.value = interval
    }
}
