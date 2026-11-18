package io.github.openflocon.flocondesktop.features.dashboard.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DashboardArrangement {

    @Immutable
    data object Adaptive : DashboardArrangement

    @Immutable
    data class Fixed(val itemsPerRow: Int) : DashboardArrangement
}
