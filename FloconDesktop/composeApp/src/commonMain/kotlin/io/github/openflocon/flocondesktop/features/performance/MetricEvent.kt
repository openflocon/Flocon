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

fun previewMetricsEvent() = MetricEvent(
    timestamp = "10:55:38.123",
    ramMb = "150",
    fps = "60.0",
    jankPercentage = "0.0%",
    battery = "85%",
    screenshotPath = null
)