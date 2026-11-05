package io.github.openflocon.domain.timeline.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * The zone of the timeline the Flocon window is asked to show, held until the timeline applies it.
 * A new request replaces the pending one.
 */
interface TimelineFocusRepository {
    fun requestFocus(
        target: DeviceIdAndPackageNameDomainModel,
        sessionStart: Long?,
        event: TimelineEventRef?,
        fromMs: Long,
        toMs: Long,
    )

    /** The requests to apply, the expired ones are never emitted */
    fun observeFocus(): Flow<TimelineFocusRequestDomainModel>

    /** Clears [request] once applied, unless a newer one replaced it meanwhile */
    fun consumeFocus(request: TimelineFocusRequestDomainModel)
}
