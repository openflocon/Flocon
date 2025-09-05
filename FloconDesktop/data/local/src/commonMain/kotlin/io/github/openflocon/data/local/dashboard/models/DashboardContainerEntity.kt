package io.github.openflocon.data.local.dashboard.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DashboardEntity::class,
            parentColumns = ["dashboardId"],
            childColumns = ["dashboardId"],
            onDelete = ForeignKey.CASCADE, // If a dashboard is deleted, its sections are also deleted
        ),
    ],
    indices = [
        Index(value = ["dashboardId"]),
        Index(
            value = ["dashboardId", "containerOrder"],
            unique = true,
        ),
    ],
)
data class DashboardContainerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dashboardId: String,
    val containerOrder: Int,
    val containerConfig: ContainerConfigEntity,
    val name: String,
)
