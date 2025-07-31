package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model

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
            onDelete = ForeignKey.CASCADE, // Si un dashboard est supprimé, ses sections le sont aussi
        ),
    ],
    indices = [
        Index(value = ["dashboardId"]),
        Index(
            value = ["dashboardId", "sectionOrder"],
            unique = true,
        ),
    ],
)
data class DashboardSectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dashboardId: String,
    val sectionOrder: Int,
    val name: String,
)
