package com.flocon.data.remote.dashboard.mapper

import com.flocon.data.remote.dashboard.models.ButtonConfigDataModel
import com.flocon.data.remote.dashboard.models.CheckBoxConfigDataModel
import com.flocon.data.remote.dashboard.models.ContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.DashboardConfigDataModel
import com.flocon.data.remote.dashboard.models.DashboardElementDataModel
import com.flocon.data.remote.dashboard.models.DashboardContainerDataModel
import com.flocon.data.remote.dashboard.models.FormContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.SectionContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.TextConfigDataModel
import com.flocon.data.remote.dashboard.models.TextFieldConfigDataModel
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel

fun toDomain(model: DashboardConfigDataModel): DashboardDomainModel = DashboardDomainModel(
    dashboardId = model.dashboardId,
    containers = model.containers.map {
        toDomain(it)
    },
)

fun toDomain(model: DashboardContainerDataModel): DashboardContainerDomainModel = DashboardContainerDomainModel(
    name = model.name,
    elements = model.elements.mapNotNull { toDomain(it) },
    containerConfig = model.containerConfig.toDomain()
)

fun toDomain(model: DashboardElementDataModel): DashboardElementDomainModel? {
    model.text?.let {
        return toDomain(it)
    }
    model.button?.let {
        return toDomain(it)
    }
    model.textField?.let {
        return toDomain(it)
    }
    model.checkBox?.let {
        return toDomain(it)
    }
    return null
}

fun toDomain(model: ButtonConfigDataModel): DashboardElementDomainModel.Button = DashboardElementDomainModel.Button(
    text = model.text,
    id = model.id,
)

fun toDomain(model: TextConfigDataModel): DashboardElementDomainModel.Text = DashboardElementDomainModel.Text(
    label = model.label,
    value = model.value,
    color = model.color,
)

fun toDomain(model: TextFieldConfigDataModel): DashboardElementDomainModel.TextField = DashboardElementDomainModel.TextField(
    value = model.value,
    label = model.label,
    placeHolder = model.placeHolder,
    id = model.id,
)

fun toDomain(model: CheckBoxConfigDataModel): DashboardElementDomainModel.CheckBox = DashboardElementDomainModel.CheckBox(
    value = model.value,
    label = model.label,
    id = model.id,
)

fun ContainerConfigDataModel.toDomain(): ContainerConfigDomainModel = when (this) {
    is FormContainerConfigDataModel -> FormContainerConfigDomainModel(
        formId = formId,
        submitText = submitText,
    )

    is SectionContainerConfigDataModel -> SectionContainerConfigDomainModel
}
