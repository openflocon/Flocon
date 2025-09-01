package io.github.openflocon.domain.dashboard.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

data class DashboardContainerDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
    val containerConfig: ContainerConfigDomainModel,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("containerType")
abstract class ContainerConfigDomainModel {
    abstract val containerType: DashboardContainerType
}

@Serializable
@SerialName("FORM")
data class FormContainerConfigDomainModel(
    val formId: String,
    val submitText: String,
    override val containerType: DashboardContainerType = DashboardContainerType.FORM
) : ContainerConfigDomainModel()

@Serializable
@SerialName("SECTION")
data object SectionContainerConfigDomainModel: ContainerConfigDomainModel() {
    override val containerType: DashboardContainerType = DashboardContainerType.SECTION
}
