package com.flocon.data.remote.dashboard.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class DashboardConfigDataModel(
    val dashboardId: String,
    val containers: List<DashboardContainerDataModel>,
)

@Serializable
data class DashboardContainerDataModel(
    val name: String,
    val elements: List<DashboardElementDataModel>,
    val containerConfig: ContainerConfigDataModel
)

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("containerType")
@Serializable
sealed interface ContainerConfigDataModel

@Serializable
@SerialName("FORM")
data class FormContainerConfigDataModel(
    val formId: String,
    val submitText: String,
) : ContainerConfigDataModel

@Serializable
@SerialName("SECTION")
data object SectionContainerConfigDataModel : ContainerConfigDataModel

@Serializable
data class DashboardElementDataModel(
    val button: ButtonConfigDataModel? = null,
    val text: TextConfigDataModel? = null,
    val plainText: PlainTextConfigDataModel? = null,
    val textField: TextFieldConfigDataModel? = null,
    val checkBox: CheckBoxConfigDataModel? = null,
    val label: LabelConfigDataModel? = null,
    val markdown: MarkdownConfigDataModel? = null,
    val html: HtmlConfigDataModel? = null,
)

@Serializable
data class HtmlConfigDataModel(
    val label: String,
    val value: String,
)

@Serializable
data class MarkdownConfigDataModel(
    val label: String,
    val value: String,
)

@Serializable
data class ButtonConfigDataModel(
    val text: String,
    val id: String,
)

@Serializable
data class TextFieldConfigDataModel(
    val label: String,
    val placeHolder: String? = null,
    val value: String,
    val id: String,
)

@Serializable
data class CheckBoxConfigDataModel(
    val label: String,
    val value: Boolean,
    val id: String,
)

@Serializable
data class TextConfigDataModel(
    val label: String,
    val value: String,
    val color: Int? = null,
)

@Serializable
data class LabelConfigDataModel(
    val label: String,
    val color: Int? = null,
)

@Serializable
data class PlainTextConfigDataModel(
    val label: String,
    val value: String,
    val type: String,
)
