package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "analyticsTableId"]),
    ],
)
data class AnalyticsItemEntity(
    @PrimaryKey
    val itemId: String,
    val analyticsTableId: String,
    val deviceId: String,
    val createdAt: Long,
    val eventName: String,
    val propertiesColumnsNames: List<String>,
    val propertiesValues: List<String>,
)
