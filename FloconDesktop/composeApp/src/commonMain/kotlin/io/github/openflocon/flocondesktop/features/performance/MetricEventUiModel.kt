package io.github.openflocon.flocondesktop.features.performance

import kotlinx.serialization.Serializable

@Serializable
data class MetricEventUiModel(
    val timestamp: String,
    val ramMb: String?,
    val rawRamMb: Long?,
    val fps: String,
    val rawFps: Double,
    val jankPercentage: String,
    val rawJankPercentage: Double,
    val battery: String,
    val screenshotPath: String?,
    val isFpsDrop: Boolean,
)

fun previewMetricsEvent() = MetricEventUiModel(
    timestamp = "10:55:38.123",
    ramMb = "150",
    rawRamMb = 150L,
    fps = "60.0",
    rawFps = 60.0,
    jankPercentage = "0.0%",
    rawJankPercentage = 0.0,
    battery = "85%",
    screenshotPath = null,
    isFpsDrop = false,
)