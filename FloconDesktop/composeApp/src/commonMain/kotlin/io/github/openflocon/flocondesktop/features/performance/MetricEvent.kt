package io.github.openflocon.flocondesktop.features.performance

import kotlinx.serialization.Serializable

@Serializable
data class MetricEvent(
    val timestamp: String,
    val ramMb: String,
    val fps: String,
    val jankPercentage: String,
    val battery: String,
    val screenshotPath: String?,
)
