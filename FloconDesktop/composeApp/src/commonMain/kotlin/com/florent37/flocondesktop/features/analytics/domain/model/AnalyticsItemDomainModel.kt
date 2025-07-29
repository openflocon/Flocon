package com.florent37.flocondesktop.features.analytics.domain.model

data class AnalyticsItemDomainModel(
    val itemId: String,
    val analyticsTableId: String,
    val createdAt: Long,
    val eventName: String,
    val properties: List<AnalyticsPropertyDomainModel>,
)
