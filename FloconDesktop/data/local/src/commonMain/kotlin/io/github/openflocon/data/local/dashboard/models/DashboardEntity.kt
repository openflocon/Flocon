package io.github.openflocon.data.local.dashboard.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["dashboardId"]),
        Index(value = ["deviceId", "packageName"]),
    ],
)
data class DashboardEntity(
    @PrimaryKey
    val dashboardId: String,
    val deviceId: String,
    val packageName: String,
)
