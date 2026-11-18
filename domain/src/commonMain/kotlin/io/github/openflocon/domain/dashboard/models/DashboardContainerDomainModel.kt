package io.github.openflocon.domain.dashboard.models

data class DashboardContainerDomainModel(
    val name: String,
    val elements: List<DashboardElementDomainModel>,
    val containerConfig: ContainerConfigDomainModel,
)

sealed interface ContainerConfigDomainModel

data class FormContainerConfigDomainModel(
    val formId: String,
    val submitText: String,
) : ContainerConfigDomainModel

data object SectionContainerConfigDomainModel : ContainerConfigDomainModel
