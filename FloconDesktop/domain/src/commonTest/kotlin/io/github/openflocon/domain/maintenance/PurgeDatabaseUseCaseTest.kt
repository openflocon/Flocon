package io.github.openflocon.domain.maintenance

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.maintenance.usecase.AutoPurgeDatabaseUseCase
import io.github.openflocon.domain.maintenance.usecase.PurgeDatabaseUseCase
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import io.github.openflocon.domain.models.maintenance.PurgeInterval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PurgeDatabaseUseCaseTest {

    private class FakeRepository : DatabaseMaintenanceRepository {
        var purgedBefore: Long? = null
        override val autoPurgeInterval = MutableStateFlow(AutoPurgeInterval.Never)
        override suspend fun setAutoPurgeInterval(interval: AutoPurgeInterval) {
            autoPurgeInterval.value = interval
        }
        override suspend fun getDatabaseSizeBytes() = 0L
        override suspend fun purgeDataBefore(beforeTimestampMs: Long) {
            purgedBefore = beforeTimestampMs
        }
    }

    private val repository = FakeRepository()
    private val useCase = PurgeDatabaseUseCase(repository)

    @Test
    fun defaultIntervalIsOneWeek() {
        assertEquals(PurgeInterval.OneWeek, PurgeInterval.DEFAULT)
    }

    @Test
    fun purgesWhatIsOlderThanTheInterval() = runTest {
        val now = 10L * 24 * 60 * 60 * 1000

        useCase(PurgeInterval.OneWeek, nowMs = now)

        assertEquals(3L * 24 * 60 * 60 * 1000, repository.purgedBefore)
    }

    @Test
    fun allPurgesEverything() = runTest {
        useCase(PurgeInterval.Everything, nowMs = 1_000L)

        assertEquals(Long.MAX_VALUE, repository.purgedBefore)
    }

    @Test
    fun autoPurgeDoesNothingWhenNever() = runTest {
        AutoPurgeDatabaseUseCase(repository)(nowMs = 10L * 24 * 60 * 60 * 1000)

        assertEquals(null, repository.purgedBefore)
    }

    @Test
    fun autoPurgeDeletesWhatIsOlderThanTheChosenInterval() = runTest {
        repository.setAutoPurgeInterval(AutoPurgeInterval.ThreeDays)

        AutoPurgeDatabaseUseCase(repository)(nowMs = 10L * 24 * 60 * 60 * 1000)

        assertEquals(7L * 24 * 60 * 60 * 1000, repository.purgedBefore)
    }

    @Test
    fun unknownStoredValueFallsBackToNever() {
        assertEquals(AutoPurgeInterval.Never, AutoPurgeInterval.fromDays(42))
        assertEquals(AutoPurgeInterval.Never, AutoPurgeInterval.fromDays(null))
        assertEquals(AutoPurgeInterval.OneWeek, AutoPurgeInterval.fromDays(7))
    }
}
