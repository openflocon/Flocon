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
    val isImage: Boolean = false, // network only: the response is an image
    val hasError: Boolean = false, // network only: the call failed (exception, cancelled…), whatever its http code
    val responseByteSize: Long? = null, // network only: size of the response in bytes, null while waiting for it
    val level: String? = null, // log only: DEBUG, INFO, WARNING or ERROR as stored
    // database only: the transaction of the query, with the queries it ran and its first and last times (all its statements included)
    val transactionId: String? = null,
    val txQueryCount: Int? = null,
    val txStart: Long? = null,
    val txEnd: Long? = null,
    val isTransactionControl: Boolean = false, // database only: BEGIN, COMMIT, ROLLBACK...
)

// result row of the summary query: one per kind
data class TimelineSummaryRowEntity(
    val kind: String,
    val count: Int,
    val errors: Int,
    val firstTs: Long?,
    val lastTs: Long?,
)

// result row of the facet counts query: one per facet and value
data class TimelineFacetCountEntity(
    val facet: String,
    val value: String,
    val count: Int,
)

// result row of the session counts query
data class TimelineSessionCountEntity(
    val sessionStart: Long,
    val count: Int,
)
