package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.mapper

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementButton
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementCheckBox
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementPlainText
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementText
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementTextField
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardElementDomainModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardId
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardSectionDomainModel

internal fun DashboardDomainModel.toEntity(deviceId: DeviceId): DashboardEntity = DashboardEntity(
    deviceId = deviceId,
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
