package io.github.openflocon.data.local.dashboard.mapper

import io.github.openflocon.data.local.dashboard.models.ContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.DashboardContainerEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardEntity
import io.github.openflocon.data.local.dashboard.models.FormContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.LocalDashboardElement
import io.github.openflocon.data.local.dashboard.models.SectionContainerConfigEntity
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.serialization.json.Json

internal fun DashboardDomainModel.toEntity(
    deviceId: DeviceId,
    packageName: String
): DashboardEntity = DashboardEntity(
    deviceId = deviceId,
    packageName = packageName,
    dashboardId = dashboardId,
)

internal fun DashboardContainerDomainModel.toEntity(
    dashboardId: DashboardId,
    index: Int,
): DashboardContainerEntity = DashboardContainerEntity(
    name = this.name,
    dashboardId = dashboardId,
    containerOrder = index,
    containerConfig = this.containerConfig.toEntity()
)

internal fun DashboardElementDomainModel.toEntity(
    containerId: Long,
    index: Int,
    json: Json,
): DashboardElementEntity {
    val element =
        when (this) {
            is DashboardElementDomainModel.Button ->
                LocalDashboardElement.Button(
                    text = text,
                    actionId = id,
                )

            is DashboardElementDomainModel.Text ->
                LocalDashboardElement.Text(
                    label = label,
                    value = value,
                    color = color,
                )

            is DashboardElementDomainModel.TextField ->
                LocalDashboardElement.TextField(
                    actionId = id,
                    label = label,
                    value = value,
                    placeHolder = placeHolder,
                )

            is DashboardElementDomainModel.CheckBox ->
                LocalDashboardElement.CheckBox(
                    actionId = id,
                    label = label,
                    value = value,
                )

            is DashboardElementDomainModel.PlainText ->
                LocalDashboardElement.PlainText(
                    label = label,
                    value = value,
                    type =
                    when (type) {
                        DashboardElementDomainModel.PlainText.Type.Text ->
                            "text"

                        DashboardElementDomainModel.PlainText.Type.Json ->
                            "json"
                    },
                )

            is DashboardElementDomainModel.Label ->
                LocalDashboardElement.Label(
                    label = label,
                    color = color,
                )

            is DashboardElementDomainModel.Markdown ->
                LocalDashboardElement.Markdown(
                    label = label,
                    value = value,
                )

            is DashboardElementDomainModel.Html ->
                LocalDashboardElement.Html(
                    label = label,
                    value = value,
                )
        }

    return DashboardElementEntity(
        containerId = containerId,
        elementOrder = index,
        elementAsJson = json.encodeToString(element),
    )
}

fun ContainerConfigDomainModel.toEntity(): ContainerConfigEntity = when (this) {
    is FormContainerConfigDomainModel -> FormContainerConfigEntity(formId, submitText)
    is SectionContainerConfigDomainModel -> SectionContainerConfigEntity
}
