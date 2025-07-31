package io.github.openflocon.flocondesktop.features.analytics.ui.model

import androidx.compose.runtime.Immutable
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsTableId

@Immutable
data class DeviceAnalyticsUiModel(
    val id: AnalyticsTableId,
    val name: String,
)

fun previewDeviceAnalyticsUiModel() = DeviceAnalyticsUiModel(
    id = "0",
    name = "name",
)
