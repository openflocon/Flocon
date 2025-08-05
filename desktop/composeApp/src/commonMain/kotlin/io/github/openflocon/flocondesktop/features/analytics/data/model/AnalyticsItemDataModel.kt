package io.github.openflocon.flocondesktop.features.analytics.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsItemDataModel(
    val id: String,
    val analyticsTableId: String,
    val eventName: String,
    val createdAt: Long,
    val properties: List<AnalyticsPropertyDataModel>? = emptyList(),
)
