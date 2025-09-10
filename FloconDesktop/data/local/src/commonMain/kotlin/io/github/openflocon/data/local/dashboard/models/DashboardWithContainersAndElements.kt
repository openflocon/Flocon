package io.github.openflocon.data.local.dashboard.models

import androidx.room.Embedded
import androidx.room.Relation

data class DashboardWithContainersAndElements(
    @Embedded
    val dashboard: DashboardEntity,

    @Relation(
        parentColumn = "dashboardId",
        entityColumn = "dashboardId",
        entity = DashboardContainerEntity::class,
    )
    val containersWithElements: List<ContainerWithElements>,
)
