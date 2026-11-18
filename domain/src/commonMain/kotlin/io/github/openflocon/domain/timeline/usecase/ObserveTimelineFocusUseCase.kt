package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineFocusRepository
import kotlinx.coroutines.flow.Flow

class ObserveTimelineFocusUseCase(
    private val timelineFocusRepository: TimelineFocusRepository,
) {
    operator fun invoke(): Flow<TimelineFocusRequestDomainModel> = timelineFocusRepository.observeFocus()
}
