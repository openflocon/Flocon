package com.florent37.flocondesktop.features.dashboard.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DashboardsStateUiModel {
    @Immutable
    data object Empty : DashboardsStateUiModel

    @Immutable
    data object Loading : DashboardsStateUiModel

    @Immutable
    data class WithContent(
        val dashboards: List<DeviceDashboardUiModel>,
        val selected: DeviceDashboardUiModel,
    ) : DashboardsStateUiModel
}

fun previewDashboardsStateUiModel() = DashboardsStateUiModel.WithContent(
    dashboards = listOf(
        previewDeviceDashboardUiModel(),
        previewDeviceDashboardUiModel(),
        previewDeviceDashboardUiModel(),
    ),
    selected = previewDeviceDashboardUiModel(),
)
