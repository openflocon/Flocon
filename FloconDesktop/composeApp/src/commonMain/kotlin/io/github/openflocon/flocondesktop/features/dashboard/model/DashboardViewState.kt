package io.github.openflocon.flocondesktop.features.dashboard.model

data class DashboardViewState(
    val items: List<DashboardContainerViewState>,
)

fun previewDashboardViewState() = DashboardViewState(
    items = listOf(
        previewDashboardContainerViewState(),
        previewDashboardContainerViewState(),
        previewDashboardContainerViewState(),
        previewDashboardContainerViewState(),
    ),
)
