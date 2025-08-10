package io.github.openflocon.flocon.plugins.analytics.model

data class AnalyticsEvent(
    val eventName: String,
    val properties: List<AnalyticsPropertiesConfig>,
) {
    constructor(
        eventName: String,
        vararg properties: AnalyticsPropertiesConfig,
    ) : this(eventName, properties.toList())
}