package io.github.openflocon.data.local.dashboard.mapper

import io.github.openflocon.data.local.dashboard.models.ContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.ContainerWithElements
import io.github.openflocon.data.local.dashboard.models.DashboardElement
import io.github.openflocon.data.local.dashboard.models.DashboardElementButton
import io.github.openflocon.data.local.dashboard.models.DashboardElementCheckBox
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementLabel
import io.github.openflocon.data.local.dashboard.models.DashboardElementPlainText
import io.github.openflocon.data.local.dashboard.models.DashboardElementText
import io.github.openflocon.data.local.dashboard.models.DashboardElementTextField
import io.github.openflocon.data.local.dashboard.models.DashboardWithContainersAndElements
import io.github.openflocon.data.local.dashboard.models.FormContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.SectionContainerConfigEntity
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import kotlinx.serialization.json.Json

internal fun DashboardWithContainersAndElements.toDomain(): DashboardDomainModel =
        DashboardDomainModel(
                dashboardId = dashboard.dashboardId,
                containers = containersWithElements.mapNotNull { it.toDomain() },
        )

internal fun ContainerWithElements.toDomain(): DashboardContainerDomainModel? {
    return DashboardContainerDomainModel(
            name = this.container?.name ?: return null,
            elements = elements.mapNotNull { it.toDomain() },
            containerConfig = this.container.containerConfig.toDomain()
    )
}

internal fun DashboardElementEntity.toDomain(): DashboardElementDomainModel? {
    return try {
        when (val element = Json.decodeFromString<DashboardElement>(this.elementAsJson)) {
            is DashboardElementButton ->
                    DashboardElementDomainModel.Button(
                            text = element.text,
                            id = element.actionId,
                    )
            is DashboardElementText ->
                    DashboardElementDomainModel.Text(
                            label = element.label,
                            value = element.value,
                            color = element.color,
                    )
            is DashboardElementLabel ->
                    DashboardElementDomainModel.Label(
                            label = element.label,
                            color = element.color,
                    )
            is DashboardElementPlainText ->
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
            is DashboardElementTextField ->
                    DashboardElementDomainModel.TextField(
                            label = element.label,
                            value = element.value,
                            placeHolder = element.placeHolder,
                            id = element.actionId,
                    )
            is DashboardElementCheckBox ->
                    DashboardElementDomainModel.CheckBox(
                            label = element.label,
                            value = element.value,
                            id = element.actionId,
                    )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun ContainerConfigEntity.toDomain(): ContainerConfigDomainModel =
        when (this) {
            is FormContainerConfigEntity -> FormContainerConfigDomainModel(formId, submitText)
            is SectionContainerConfigEntity -> SectionContainerConfigDomainModel
        }
