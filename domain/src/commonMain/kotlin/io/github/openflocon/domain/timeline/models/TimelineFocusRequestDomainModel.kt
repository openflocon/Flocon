package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

/**
 * What the timeline is asked to show: the zone of a session to look at, and the event to select if there is one.
 * Times are the epoch milliseconds of the phone.
 */
data class TimelineFocusRequestDomainModel(
    // the device and app the zone belongs to: the timeline applies the request once it is the one displayed
    val target: DeviceIdAndPackageNameDomainModel,
    // the start of the session holding the zone, null when no session is known
    val sessionStart: Long?,
    // the event to select, null: only the zone is shown
    val event: TimelineEventRef?,
    val fromMs: Long,
    val toMs: Long,
    val requestedAtMs: Long,
)
