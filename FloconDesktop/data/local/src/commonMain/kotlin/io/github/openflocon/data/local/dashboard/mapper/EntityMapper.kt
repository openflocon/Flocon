package io.github.openflocon.data.local.dashboard.mapper

import io.github.openflocon.data.local.dashboard.models.ContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.DashboardContainerEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementButton
import io.github.openflocon.data.local.dashboard.models.DashboardElementCheckBox
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementLabel
import io.github.openflocon.data.local.dashboard.models.DashboardElementPlainText
import io.github.openflocon.data.local.dashboard.models.DashboardElementText
import io.github.openflocon.data.local.dashboard.models.DashboardElementTextField
import io.github.openflocon.data.local.dashboard.models.DashboardEntity
import io.github.openflocon.data.local.dashboard.models.FormContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.SectionContainerConfigEntity
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun DashboardDomainModel.toEntity(
        deviceId: DeviceId,
        packageName: String
): DashboardEntity =
        DashboardEntity(
                deviceId = deviceId,
                packageName = packageName,
                dashboardId = dashboardId,
        )

internal fun DashboardContainerDomainModel.toEntity(
        dashboardId: DashboardId,
        index: Int,
): DashboardContainerEntity =
        DashboardContainerEntity(
                name = this.name,
                dashboardId = dashboardId,
                containerOrder = index,
                containerConfig = this.containerConfig.toEntity()
        )

internal fun DashboardElementDomainModel.toEntity(
        containerId: Long,
        index: Int,
): DashboardElementEntity {
    val element =
            when (this) {
                is DashboardElementDomainModel.Button ->
                        DashboardElementButton(
                                text = text,
                                actionId = id,
                        )
                is DashboardElementDomainModel.Text ->
                        DashboardElementText(
                                label = label,
                                value = value,
                                color = color,
                        )
                is DashboardElementDomainModel.TextField ->
                        DashboardElementTextField(
                                actionId = id,
                                label = label,
                                value = value,
                                placeHolder = placeHolder,
                        )
                is DashboardElementDomainModel.CheckBox ->
                        DashboardElementCheckBox(
                                actionId = id,
                                label = label,
                                value = value,
                        )
                is DashboardElementDomainModel.PlainText ->
                        DashboardElementPlainText(
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
                        DashboardElementLabel(
                                label = label,
                                color = color,
                        )
            }

    return DashboardElementEntity(
            containerId = containerId,
            elementOrder = index,
            elementAsJson = Json.encodeToString(element),
    )
}

fun ContainerConfigDomainModel.toEntity(): ContainerConfigEntity =
        when (this) {
            is FormContainerConfigDomainModel -> FormContainerConfigEntity(formId, submitText)
            is SectionContainerConfigDomainModel -> SectionContainerConfigEntity
        }
