package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineFocusRepository

class ConsumeTimelineFocusUseCase(
    private val timelineFocusRepository: TimelineFocusRepository,
) {
    operator fun invoke(request: TimelineFocusRequestDomainModel) = timelineFocusRepository.consumeFocus(request)
}
