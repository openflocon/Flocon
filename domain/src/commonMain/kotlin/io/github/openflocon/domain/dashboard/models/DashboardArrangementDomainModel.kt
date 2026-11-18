package io.github.openflocon.domain.dashboard.models

sealed interface DashboardArrangementDomainModel {
    data object Adaptive : DashboardArrangementDomainModel
    data class Fixed(val itemsPerRow: Int) : DashboardArrangementDomainModel
}
