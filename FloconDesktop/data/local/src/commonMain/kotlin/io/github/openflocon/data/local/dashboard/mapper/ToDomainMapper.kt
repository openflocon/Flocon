package io.github.openflocon.data.local.dashboard.mapper

import io.github.openflocon.data.local.dashboard.models.ContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.ContainerWithElements
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardWithContainersAndElements
import io.github.openflocon.data.local.dashboard.models.FormContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.LocalDashboardElement
import io.github.openflocon.data.local.dashboard.models.SectionContainerConfigEntity
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import kotlinx.serialization.json.Json

internal fun DashboardWithContainersAndElements.toDomain(json: Json): DashboardDomainModel = DashboardDomainModel(
    dashboardId = dashboard.dashboardId,
    containers = containersWithElements.mapNotNull {
        it.toDomain(
            json = json
        )
    },
)

internal fun ContainerWithElements.toDomain(json: Json): DashboardContainerDomainModel? {
    return DashboardContainerDomainModel(
        name = this.container?.name ?: return null,
        elements = elements.mapNotNull {
            it.toDomain(
                json = json
            )
        },
        containerConfig = this.container.containerConfig.toDomain()
    )
}

internal fun DashboardElementEntity.toDomain(json: Json): DashboardElementDomainModel? = try {
    when (val element = json.decodeFromString<LocalDashboardElement>(this.elementAsJson)) {
        is LocalDashboardElement.Button ->
            DashboardElementDomainModel.Button(
                text = element.text,
                id = element.actionId,
            )

        is LocalDashboardElement.Text ->
            DashboardElementDomainModel.Text(
                label = element.label,
                value = element.value,
                color = element.color,
            )

        is LocalDashboardElement.Label ->
            DashboardElementDomainModel.Label(
                label = element.label,
                color = element.color,
            )

        is LocalDashboardElement.PlainText ->
            DashboardElementDomainModel.PlainText(
                label = element.label,
                value = element.value,
                type =
                when (element.type) {
                    "text" -> DashboardElementDomainModel.PlainText.Type.Text
                    "json" -> DashboardElementDomainModel.PlainText.Type.Json
                    else -> DashboardElementDomainModel.PlainText.Type.Text
                },
            )

        is LocalDashboardElement.TextField ->
            DashboardElementDomainModel.TextField(
                label = element.label,
                value = element.value,
                placeHolder = element.placeHolder,
                id = element.actionId,
            )

        is LocalDashboardElement.CheckBox ->
            DashboardElementDomainModel.CheckBox(
                label = element.label,
                value = element.value,
                id = element.actionId,
            )

        is LocalDashboardElement.Markdown ->
            DashboardElementDomainModel.Markdown(
                label = element.label,
                value = element.value,
            )
    }
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun ContainerConfigEntity.toDomain(): ContainerConfigDomainModel = when (this) {
    is FormContainerConfigEntity -> FormContainerConfigDomainModel(formId, submitText)
    is SectionContainerConfigEntity -> SectionContainerConfigDomainModel
}
