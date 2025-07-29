package com.florent37.flocondesktop.features.dashboard.ui.model

import androidx.compose.runtime.Immutable
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId

@Immutable
data class DeviceDashboardUiModel(
    val id: DashboardId,
)

fun previewDeviceDashboardUiModel() = DeviceDashboardUiModel(
    id = "id",
)
