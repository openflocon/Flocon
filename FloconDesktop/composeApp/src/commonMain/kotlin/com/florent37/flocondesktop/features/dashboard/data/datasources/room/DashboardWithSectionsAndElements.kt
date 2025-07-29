package com.florent37.flocondesktop.features.dashboard.data.datasources.room

import androidx.room.Embedded
import androidx.room.Relation
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity

data class DashboardWithSectionsAndElements(
    @Embedded
    val dashboard: DashboardEntity,

    @Relation(
        parentColumn = "dashboardId",
        entityColumn = "dashboardId",
        entity = DashboardSectionEntity::class,
    )
    val sectionsWithElements: List<SectionWithElements>,
)
