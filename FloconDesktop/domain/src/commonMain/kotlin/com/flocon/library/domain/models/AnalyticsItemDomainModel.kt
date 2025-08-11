package com.flocon.library.domain.models

data class AnalyticsItemDomainModel(
    val itemId: String,
    val analyticsTableId: String,
    val createdAt: Long,
    val eventName: String,
    val properties: List<AnalyticsPropertyDomainModel>,
)
