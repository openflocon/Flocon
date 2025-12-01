package io.github.openflocon.flocondesktop.features.dashboard.model

sealed interface DashboardArrangement {

    data object Adaptive : DashboardArrangement

    data class Fixed(val itemsPerRow: Int) : DashboardArrangement
}
