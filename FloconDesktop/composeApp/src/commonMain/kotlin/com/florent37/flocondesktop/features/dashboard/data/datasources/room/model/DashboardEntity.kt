package com.florent37.flocondesktop.features.dashboard.data.datasources.room.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["dashboardId"]),
        Index(value = ["deviceId"]),
    ],
)
data class DashboardEntity(
    @PrimaryKey
    val dashboardId: String,
    val deviceId: String,
)
