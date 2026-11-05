package io.github.openflocon.domain.timeline.usecase

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.repository.TimelineFocusRepository

class RequestTimelineFocusUseCase(
    private val timelineFocusRepository: TimelineFocusRepository,
) {
    operator fun invoke(
        target: DeviceIdAndPackageNameDomainModel,
        sessionStart: Long?,
        event: TimelineEventRef?,
        fromMs: Long,
        toMs: Long,
    ) = timelineFocusRepository.requestFocus(target, sessionStart, event, fromMs, toMs)
}
