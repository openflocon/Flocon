package io.github.openflocon.data.local.timeline.models

// result row of the union query, not a table
data class TimelineRowEntity(
    val kind: String,
    val id: String,
    val ts: Long,
    val title: String,
    val subtitle: String?,
    val appInstance: Long?,
)
