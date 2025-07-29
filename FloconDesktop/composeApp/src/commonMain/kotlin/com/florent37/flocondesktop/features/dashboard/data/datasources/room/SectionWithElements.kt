package com.florent37.flocondesktop.features.dashboard.data.datasources.room

import androidx.room.Embedded
import androidx.room.Relation
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity

data class SectionWithElements(
    @Embedded
    val section: DashboardSectionEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "sectionId",
        entity = DashboardElementEntity::class,
    )
    val elements: List<DashboardElementEntity>,
)
