package io.github.openflocon.data.local.dashboard.models

import androidx.room.Embedded
import androidx.room.Relation

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
