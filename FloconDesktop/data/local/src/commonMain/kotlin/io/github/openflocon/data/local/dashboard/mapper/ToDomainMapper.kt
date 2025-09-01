package io.github.openflocon.data.local.dashboard.mapper

import io.github.openflocon.data.local.dashboard.models.DashboardWithContainersAndElements
import io.github.openflocon.data.local.dashboard.models.ContainerWithElements
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel

internal fun DashboardWithContainersAndElements.toDomain(): DashboardDomainModel = DashboardDomainModel(
    dashboardId = dashboard.dashboardId,
    containers = containersWithElements.mapNotNull {
        it.toDomain()
    },
)

internal fun ContainerWithElements.toDomain(): DashboardContainerDomainModel? {
    return DashboardContainerDomainModel(
        name = this.container?.name ?: return null,
        elements = elements.mapNotNull { it.toDomain() },
        containerConfig = this.container.containerConfig
    )
}

internal fun DashboardElementEntity.toDomain(): DashboardElementDomainModel? {
    this.text?.let {
        return DashboardElementDomainModel.Text(
            label = it.label,
            value = it.value,
            color = it.color,
        )
    }
    this.button?.let {
        return DashboardElementDomainModel.Button(
            text = it.text,
            id = it.actionId,
        )
    }
    this.textField?.let {
        return DashboardElementDomainModel.TextField(
            label = it.label,
            value = it.value,
            placeHolder = it.placeHolder,
            id = it.actionId,
        )
    }
    this.checkBox?.let {
        return DashboardElementDomainModel.CheckBox(
            label = it.label,
            value = it.value,
            id = it.actionId,
        )
    }
    return null
}
