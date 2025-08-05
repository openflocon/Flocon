package io.github.openflocon.flocondesktop.features.dashboard.ui.model

data class DashboardViewState(
    val items: List<DashboardItemViewState>,
)

fun previewDashboardViewState() = DashboardViewState(
    items = listOf(
        previewDashboardItemViewState(),
        previewDashboardItemViewState(),
        previewDashboardItemViewState(),
        previewDashboardItemViewState(),
    ),
)
