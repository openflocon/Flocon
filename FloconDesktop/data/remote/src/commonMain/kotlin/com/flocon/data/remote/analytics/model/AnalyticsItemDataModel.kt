package com.flocon.data.remote.analytics.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsItemDataModel(
    @SerialName("id")
    val id: String,
    @SerialName("analytics_table_id")
    val analyticsTableId: String,
    @SerialName("event_name")
    val eventName: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("properties")
    val properties: List<AnalyticsPropertyDataModel>? = emptyList(),
)
