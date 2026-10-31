package io.github.openflocon.flocon.analytics.model

data class AnalyticsEvent(
    val eventName: String,
    val analyticsTableId: String,
    val properties: List<AnalyticsPropertiesConfig>,
) {
    constructor(
        eventName: String,
        analyticsTableId: String,
        vararg properties: AnalyticsPropertiesConfig,
    ) : this(
        eventName = eventName,
        analyticsTableId = analyticsTableId,
        properties = properties.toList()
    )
}
