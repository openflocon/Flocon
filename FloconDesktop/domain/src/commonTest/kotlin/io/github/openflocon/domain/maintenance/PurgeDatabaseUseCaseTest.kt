package io.github.openflocon.domain.maintenance

import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.maintenance.usecase.PurgeDatabaseUseCase
import io.github.openflocon.domain.models.maintenance.PurgeInterval
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PurgeDatabaseUseCaseTest {

    private class FakeRepository : DatabaseMaintenanceRepository {
        var purgedBefore: Long? = null
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
        useCase(PurgeInterval.All, nowMs = 1_000L)

        assertEquals(Long.MAX_VALUE, repository.purgedBefore)
    }
}
