package io.github.openflocon.data.local.timeline.models

// result row of the union query, not a table
data class TimelineRowEntity(
    val kind: String,
    val id: String,
    val ts: Long,
    val title: String,
    val subtitle: String?,
    val appInstance: Long?,
    val method: String?,
    val statusCode: Int?,
    val durationMs: Double?,
    val sessionStart: Long?,
)

// result row of the summary query: one per kind
data class TimelineSummaryRowEntity(
    val kind: String,
    val count: Int,
    val errors: Int,
    val firstTs: Long?,
    val lastTs: Long?,
)

// result row of the session counts query
data class TimelineSessionCountEntity(
    val sessionStart: Long,
    val count: Int,
)
