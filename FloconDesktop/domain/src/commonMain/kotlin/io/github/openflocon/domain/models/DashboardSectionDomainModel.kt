package io.github.openflocon.domain.models

data class DashboardSectionDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
)
