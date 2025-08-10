package io.github.openflocon.flocon.plugins.analytics.model

data class AnalyticsItem(
    val id: String,
    val analyticsTableId: String,
    val eventName: String,
    val createdAt: Long,
    val properties: List<AnalyticsPropertiesConfig>,
)