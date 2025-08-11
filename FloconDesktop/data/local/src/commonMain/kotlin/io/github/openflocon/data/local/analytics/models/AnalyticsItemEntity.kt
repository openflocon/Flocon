package io.github.openflocon.data.local.analytics.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName", "analyticsTableId"]),
    ],
)
data class AnalyticsItemEntity(
    @PrimaryKey
    val itemId: String,
    val analyticsTableId: String,
    val deviceId: String,
    val packageName: String,
    val createdAt: Long,
    val eventName: String,
    val propertiesColumnsNames: List<String>,
    val propertiesValues: List<String>,
)
