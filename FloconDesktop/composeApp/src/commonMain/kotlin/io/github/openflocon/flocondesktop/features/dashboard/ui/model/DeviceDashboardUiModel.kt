package io.github.openflocon.flocondesktop.features.dashboard.ui.model

import androidx.compose.runtime.Immutable
import com.flocon.library.domain.models.DashboardId

@Immutable
data class DeviceDashboardUiModel(
    val id: DashboardId,
)

fun previewDeviceDashboardUiModel() = DeviceDashboardUiModel(
    id = "id",
)
