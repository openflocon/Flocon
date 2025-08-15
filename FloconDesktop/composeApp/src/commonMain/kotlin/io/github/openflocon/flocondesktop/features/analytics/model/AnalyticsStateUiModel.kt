package io.github.openflocon.flocondesktop.features.analytics.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface AnalyticsStateUiModel {
    @Immutable
    data object Empty : AnalyticsStateUiModel

    @Immutable
    data object Loading : AnalyticsStateUiModel

    @Immutable
    data class WithContent(
        val analytics: List<DeviceAnalyticsUiModel>,
        val selected: DeviceAnalyticsUiModel,
    ) : AnalyticsStateUiModel
}

fun previewAnalyticsStateUiModel() = AnalyticsStateUiModel.WithContent(
    analytics = listOf(
        previewDeviceAnalyticsUiModel(),
        previewDeviceAnalyticsUiModel(),
        previewDeviceAnalyticsUiModel(),
    ),
    selected = previewDeviceAnalyticsUiModel(),
)
