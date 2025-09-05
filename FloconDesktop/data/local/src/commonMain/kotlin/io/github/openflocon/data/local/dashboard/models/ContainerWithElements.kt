package io.github.openflocon.data.local.dashboard.models

import androidx.room.Embedded
import androidx.room.Relation

data class ContainerWithElements(
    @Embedded
    val container: DashboardContainerEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "containerId",
        entity = DashboardElementEntity::class,
    )
    val elements: List<DashboardElementEntity>,
)
