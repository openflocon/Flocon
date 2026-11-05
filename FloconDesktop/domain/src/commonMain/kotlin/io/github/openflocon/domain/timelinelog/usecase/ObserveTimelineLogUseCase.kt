package io.github.openflocon.domain.timelinelog.usecase

import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.repository.TimelineLogRepository
import kotlinx.coroutines.flow.Flow

class ObserveTimelineLogUseCase(
    private val timelineLogRepository: TimelineLogRepository,
) {
    operator fun invoke(id: Long): Flow<TimelineLogDomainModel?> = timelineLogRepository.observeLog(id)
}
