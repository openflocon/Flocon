package io.github.openflocon.flocondesktop.features.analytics.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.analytics.models.AnalyticsTableId

@Immutable
data class DeviceAnalyticsUiModel(
    val id: AnalyticsTableId,
    val name: String,
)

fun previewDeviceAnalyticsUiModel() = DeviceAnalyticsUiModel(
    id = "0",
    name = "name",
)
