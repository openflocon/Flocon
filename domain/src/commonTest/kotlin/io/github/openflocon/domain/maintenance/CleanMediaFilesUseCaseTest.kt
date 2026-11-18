package io.github.openflocon.domain.maintenance

import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.maintenance.usecase.AutoCleanMediaFilesUseCase
import io.github.openflocon.domain.maintenance.usecase.CleanMediaFilesUseCase
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import io.github.openflocon.domain.models.maintenance.MediaFilesSize
import io.github.openflocon.domain.models.maintenance.PurgeInterval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CleanMediaFilesUseCaseTest {

    private class FakeRepository : MediaFilesRepository {
        var deletedBefore: Long? = null
        override val autoCleanupInterval = MutableStateFlow(AutoPurgeInterval.Never)
        override suspend fun setAutoCleanupInterval(interval: AutoPurgeInterval) {
            autoCleanupInterval.value = interval
        }
        override suspend fun getSizes() = MediaFilesSize(screenshotsBytes = 0L, recordingsBytes = 0L)
        override suspend fun deleteFilesBefore(beforeTimestampMs: Long) {
            deletedBefore = beforeTimestampMs
        }
    }

    private val repository = FakeRepository()
    private val day = 24L * 60 * 60 * 1000

    @Test
    fun cleansWhatIsOlderThanTheInterval() = runTest {
        CleanMediaFilesUseCase(repository)(PurgeInterval.OneWeek, nowMs = 10 * day)

        assertEquals(3 * day, repository.deletedBefore)
    }

    @Test
    fun everythingCleansEverything() = runTest {
        CleanMediaFilesUseCase(repository)(PurgeInterval.Everything, nowMs = 1_000L)

        assertEquals(Long.MAX_VALUE, repository.deletedBefore)
    }

    @Test
    fun autoCleanDoesNothingWhenNever() = runTest {
        AutoCleanMediaFilesUseCase(repository)(nowMs = 10 * day)

        assertEquals(null, repository.deletedBefore)
    }

    @Test
    fun autoCleanDeletesWhatIsOlderThanTheChosenInterval() = runTest {
        repository.setAutoCleanupInterval(AutoPurgeInterval.ThreeDays)

        AutoCleanMediaFilesUseCase(repository)(nowMs = 10 * day)

        assertEquals(7 * day, repository.deletedBefore)
    }
}
