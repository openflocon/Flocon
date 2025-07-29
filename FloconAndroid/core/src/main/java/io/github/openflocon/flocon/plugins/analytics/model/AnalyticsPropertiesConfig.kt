package io.github.openflocon.flocon.plugins.analytics.model

data class AnalyticsPropertiesConfig(
    val name: String,
    val value: String,
)

infix fun String.analyticsProperty(value: String) = AnalyticsPropertiesConfig(
    name = this,
    value = value,
)