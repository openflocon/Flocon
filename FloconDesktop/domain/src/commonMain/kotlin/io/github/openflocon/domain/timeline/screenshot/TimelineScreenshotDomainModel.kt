package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId

// a screenshot of the app taken for the timeline, at [timestamp] (epoch ms)
data class TimelineScreenshotDomainModel(
    val timestamp: Long,
    val filePath: String,
    val deviceId: DeviceId,
    val packageName: AppPackageName,
)
