package io.github.openflocon.domain.performance.model

data class PerformanceMetricsDomainModel(
    val ramMb: String,
    val fps: Double,
    val jankPercentage: Double,
    val battery: String,
    val screenshotPath: String?,
    val totalFrames: Int?,
    val timestamp: Long,
)
