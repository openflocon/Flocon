package io.github.openflocon.flocon.plugins.dashboard.model.config

import io.github.openflocon.flocon.plugins.dashboard.model.ContainerType

data class FormConfig(
    override val name: String,
    override val elements: List<ElementConfig>,
    override val containerType: ContainerType = ContainerType.FORM,
    val id: String,
    val submitText: String,
    val onSubmitted: (Map<String, String>) -> Unit,
) : ContainerConfig