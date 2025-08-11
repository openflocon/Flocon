package io.github.openflocon.domain.models

data class DashboardDomainModel(
    val dashboardId: DashboardId,
    val sections: List<DashboardSectionDomainModel>,
)
