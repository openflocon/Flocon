package io.github.openflocon.flocondesktop.features.dashboard.domain.model

data class DashboardSectionDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
)
