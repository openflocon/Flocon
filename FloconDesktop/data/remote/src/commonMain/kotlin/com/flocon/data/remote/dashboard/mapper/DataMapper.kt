package com.flocon.data.remote.dashboard.mapper

import com.flocon.data.remote.dashboard.models.ButtonConfigDataModel
import com.flocon.data.remote.dashboard.models.CheckBoxConfigDataModel
import com.flocon.data.remote.dashboard.models.ContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.DashboardConfigDataModel
import com.flocon.data.remote.dashboard.models.DashboardContainerDataModel
import com.flocon.data.remote.dashboard.models.DashboardElementDataModel
import com.flocon.data.remote.dashboard.models.FormContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.LabelConfigDataModel
import com.flocon.data.remote.dashboard.models.PlainTextConfigDataModel
import com.flocon.data.remote.dashboard.models.SectionContainerConfigDataModel
import com.flocon.data.remote.dashboard.models.TextConfigDataModel
import com.flocon.data.remote.dashboard.models.TextFieldConfigDataModel
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel

fun DashboardConfigDataModel.toDomain(): DashboardDomainModel = DashboardDomainModel(
    dashboardId = dashboardId,
    containers = containers.map {
        it.toDomain()
    },
)

fun DashboardContainerDataModel.toDomain(): DashboardContainerDomainModel = DashboardContainerDomainModel(
    name = name,
    elements = elements.mapNotNull { it.toDomain() },
    containerConfig = containerConfig.toDomain()
)

fun DashboardElementDataModel.toDomain(): DashboardElementDomainModel? {
    text?.let {
        return it.toDomain()
    }
    plainText?.let {
        return it.toDomain()
    }
    button?.let {
        return it.toDomain()
    }
    textField?.let {
        return it.toDomain()
    }
    checkBox?.let {
        return it.toDomain()
    }
    label?.let {
        return it.toDomain()
    }
    markdown?.let {
        return it.toDomain()
    }
    return null
}

fun ButtonConfigDataModel.toDomain(): DashboardElementDomainModel.Button = DashboardElementDomainModel.Button(
    text = text,
    id = id,
)

fun TextConfigDataModel.toDomain(): DashboardElementDomainModel.Text = DashboardElementDomainModel.Text(
    label = label,
    value = value,
    color = color,
)

fun PlainTextConfigDataModel.toDomain(): DashboardElementDomainModel.PlainText = DashboardElementDomainModel.PlainText(
    label = label,
    value = value,
    type = when (type) {
        "json" -> DashboardElementDomainModel.PlainText.Type.Json
        else -> DashboardElementDomainModel.PlainText.Type.Text
    }
)

fun LabelConfigDataModel.toDomain(): DashboardElementDomainModel.Label = DashboardElementDomainModel.Label(
    label = label,
    color = color,
)

fun TextFieldConfigDataModel.toDomain(): DashboardElementDomainModel.TextField = DashboardElementDomainModel.TextField(
    value = value,
    label = label,
    placeHolder = placeHolder,
    id = id,
)

fun CheckBoxConfigDataModel.toDomain(): DashboardElementDomainModel.CheckBox = DashboardElementDomainModel.CheckBox(
    value = value,
    label = label,
    id = id,
)

fun MarkdownConfigDataModel.toDomain(): DashboardElementDomainModel.Markdown = DashboardElementDomainModel.Markdown(
    value = value,
    label = label,
)

fun ContainerConfigDataModel.toDomain(): ContainerConfigDomainModel = when (this) {
    is FormContainerConfigDataModel -> FormContainerConfigDomainModel(
        formId = formId,
        submitText = submitText,
    )

    is SectionContainerConfigDataModel -> SectionContainerConfigDomainModel
}
