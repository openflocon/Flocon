package io.github.openflocon.data.local.dashboard.mapper

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.data.local.dashboard.models.DashboardElementButton
import io.github.openflocon.data.local.dashboard.models.DashboardElementCheckBox
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementPlainText
import io.github.openflocon.data.local.dashboard.models.DashboardElementText
import io.github.openflocon.data.local.dashboard.models.DashboardElementTextField
import io.github.openflocon.data.local.dashboard.models.DashboardEntity
import io.github.openflocon.data.local.dashboard.models.DashboardSectionEntity
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardId
import io.github.openflocon.domain.dashboard.models.DashboardSectionDomainModel

internal fun DashboardDomainModel.toEntity(deviceId: DeviceId, packageName: String): DashboardEntity = DashboardEntity(
    deviceId = deviceId,
    packageName = packageName,
    dashboardId = dashboardId,
)

internal fun DashboardSectionDomainModel.toEntity(
    dashboardId: DashboardId,
    index: Int,
): DashboardSectionEntity = DashboardSectionEntity(
    name = this.name,
    dashboardId = dashboardId,
    sectionOrder = index,
)

internal fun DashboardElementDomainModel.toEntity(
    sectionId: Long,
    index: Int,
): DashboardElementEntity = DashboardElementEntity(
    sectionId = sectionId,
    elementOrder = index,
    button = (this as? DashboardElementDomainModel.Button)?.toButtonEntity(),
    text = (this as? DashboardElementDomainModel.Text)?.toTextEntity(),
    textField = (this as? DashboardElementDomainModel.TextField)?.toTextFieldEntity(),
    checkBox = (this as? DashboardElementDomainModel.CheckBox)?.toCheckBoxEntity(),
    plainText = (this as? DashboardElementDomainModel.PlainText)?.toPlainTextEntity(),
)

internal fun DashboardElementDomainModel.Button.toButtonEntity() = DashboardElementButton(
    text = text,
    actionId = id,
)

internal fun DashboardElementDomainModel.Text.toTextEntity() = DashboardElementText(
    label = label,
    value = value,
    color = color,
)

internal fun DashboardElementDomainModel.PlainText.toPlainTextEntity() = DashboardElementPlainText(
    label = label,
    value = value,
    type = when (type) {
        DashboardElementDomainModel.PlainText.Type.Text -> "text"
        DashboardElementDomainModel.PlainText.Type.Json -> "json"
    },
)

internal fun DashboardElementDomainModel.TextField.toTextFieldEntity() = DashboardElementTextField(
    actionId = id,
    label = label,
    value = value,
    placeHolder = placeHolder,
)

internal fun DashboardElementDomainModel.CheckBox.toCheckBoxEntity() = DashboardElementCheckBox(
    actionId = id,
    label = label,
    value = value,
)
