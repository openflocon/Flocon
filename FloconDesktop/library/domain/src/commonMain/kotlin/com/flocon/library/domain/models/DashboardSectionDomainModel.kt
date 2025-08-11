package com.flocon.library.domain.models

data class DashboardSectionDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
)
