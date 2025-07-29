package com.florent37.flocondesktop.features.dashboard.domain.model

data class DashboardSectionDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
)
