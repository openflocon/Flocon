package io.github.openflocon.flocon.analytics.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsPropertiesConfig(
    @SerialName("name")
    val name: String,
    @SerialName("value")
    val value: String
)

infix fun String.analyticsProperty(value: String) = AnalyticsPropertiesConfig(
    name = this,
    value = value,
)
