package io.github.openflocon.flocondesktop.features.dashboard.data.mapper

import com.florent37.flocondesktop.features.dashboard.data.model.ButtonConfigDataModel
import com.florent37.flocondesktop.features.dashboard.data.model.CheckBoxConfigDataModel
import com.florent37.flocondesktop.features.dashboard.data.model.DashboardConfigDataModel
import com.florent37.flocondesktop.features.dashboard.data.model.DashboardElementDataModel
import com.florent37.flocondesktop.features.dashboard.data.model.SectionConfigDataModel
import com.florent37.flocondesktop.features.dashboard.data.model.TextConfigDataModel
import com.florent37.flocondesktop.features.dashboard.data.model.TextFieldConfigDataModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardElementDomainModel
import com.florent37.flocondesktop.features.dashboard.domain.model.DashboardSectionDomainModel

fun toDomain(model: DashboardConfigDataModel): DashboardDomainModel = DashboardDomainModel(
    dashboardId = model.dashboardId,
    sections = model.sections.map {
        toDomain(it)
    },
)

fun toDomain(model: SectionConfigDataModel): DashboardSectionDomainModel = DashboardSectionDomainModel(
    name = model.name,
    elements = model.elements.mapNotNull { toDomain(it) },
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
