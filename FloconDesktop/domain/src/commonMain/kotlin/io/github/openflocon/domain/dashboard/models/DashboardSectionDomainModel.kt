package io.github.openflocon.domain.dashboard.models

data class DashboardSectionDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
)
