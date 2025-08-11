package io.github.openflocon.flocondesktop.features.dashboard.ui.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.dashboard.models.DashboardId

@Immutable
data class DeviceDashboardUiModel(
    val id: DashboardId,
)

fun previewDeviceDashboardUiModel() = DeviceDashboardUiModel(
    id = "id",
)
