package io.github.openflocon.data.local.dashboard.models

import androidx.room.Embedded
import androidx.room.Relation

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
