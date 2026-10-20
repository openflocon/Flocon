package io.github.openflocon.domain.timeline.models

import io.github.openflocon.domain.device.models.AppInstance

data class TimelineEventDomainModel(
    val id: String, // the id of the event in the table of its source
    val source: TimelineSource,
    val timestamp: Long,
    val title: String,
    val subtitle: String?,
    val appInstance: AppInstance?, // null when the source does not store it
)
