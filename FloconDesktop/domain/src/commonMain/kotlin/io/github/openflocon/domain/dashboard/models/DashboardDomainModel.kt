package io.github.openflocon.domain.dashboard.models

data class DashboardDomainModel(
    val dashboardId: DashboardId,
    val sections: List<DashboardSectionDomainModel>,
)
