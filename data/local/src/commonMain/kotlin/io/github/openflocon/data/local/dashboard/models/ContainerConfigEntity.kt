package io.github.openflocon.data.local.dashboard.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("containerType")
sealed interface ContainerConfigEntity

@Serializable
@SerialName("FORM")
data class FormContainerConfigEntity(
    val formId: String,
    val submitText: String,
) : ContainerConfigEntity

@Serializable
@SerialName("SECTION")
data object SectionContainerConfigEntity : ContainerConfigEntity
