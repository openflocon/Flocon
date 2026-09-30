package io.github.openflocon.flocondesktop.features.dashboard.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DashboardViewState(
    val items: ImmutableList<DashboardContainerViewState>,
)

fun previewDashboardViewState() = DashboardViewState(
    items = persistentListOf(
        previewDashboardContainerViewState(),
        previewDashboardContainerViewState(),
        previewDashboardContainerViewState(),
        previewDashboardContainerViewState(),
    ),
)
