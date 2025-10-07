package io.github.openflocon.domain.analytics.models

import io.github.openflocon.domain.device.models.AppInstance

data class AnalyticsItemDomainModel(
    val itemId: String,
    val analyticsTableId: String,
    val createdAt: Long,
    val createdAtFormatted: String,
    val eventName: String,
    val properties: List<AnalyticsPropertyDomainModel>,
    val appInstance: AppInstance,
)
