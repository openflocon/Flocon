package com.flocon.library.domain.models

data class DashboardDomainModel(
    val dashboardId: DashboardId,
    val sections: List<DashboardSectionDomainModel>,
)
