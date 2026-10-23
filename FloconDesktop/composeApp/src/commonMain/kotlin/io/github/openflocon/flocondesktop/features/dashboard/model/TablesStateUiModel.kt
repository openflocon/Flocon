package io.github.openflocon.flocondesktop.features.dashboard.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed interface DashboardsStateUiModel {
    @Immutable
    data object Empty : DashboardsStateUiModel

    @Immutable
    data object Loading : DashboardsStateUiModel

    @Immutable
    data class WithContent(
        val dashboards: ImmutableList<DeviceDashboardUiModel>,
        val selected: DeviceDashboardUiModel,
    ) : DashboardsStateUiModel
}

fun previewDashboardsStateUiModel() = DashboardsStateUiModel.WithContent(
    dashboards = persistentListOf(
        previewDeviceDashboardUiModel(),
        previewDeviceDashboardUiModel(),
        previewDeviceDashboardUiModel(),
    ),
    selected = previewDeviceDashboardUiModel(),
)
