package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.mapper

import io.github.openflocon.domain.models.DeviceId
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementButton
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementCheckBox
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementPlainText
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementText
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementTextField
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity
import io.github.openflocon.domain.models.DashboardDomainModel
import io.github.openflocon.domain.models.DashboardElementDomainModel
import io.github.openflocon.domain.models.DashboardId
import io.github.openflocon.domain.models.DashboardSectionDomainModel

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
