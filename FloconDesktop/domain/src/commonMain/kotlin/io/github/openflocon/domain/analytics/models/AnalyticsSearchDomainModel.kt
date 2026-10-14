package io.github.openflocon.domain.analytics.models

import io.github.openflocon.domain.device.models.AppInstance

/**
 * Filters applied by the database, to read large volumes of events
 */
data class AnalyticsSearchDomainModel(
    val analyticsTableId: AnalyticsTableId?, // all the tables when null
    val text: String?, // matched on the event name, date and properties
    val eventNames: List<String>?,
    val appInstance: AppInstance?, // all the sessions when null
    val sinceTimestamp: Long?,
)

data class AnalyticsEventCountDomainModel(
    val eventName: String,
    val count: Int,
)
