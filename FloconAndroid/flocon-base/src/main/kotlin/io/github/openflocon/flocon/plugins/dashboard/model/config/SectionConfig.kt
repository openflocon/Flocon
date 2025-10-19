package io.github.openflocon.flocon.plugins.dashboard.model.config

import io.github.openflocon.flocon.plugins.dashboard.model.ContainerType

data class SectionConfig(
    override val name: String,
    override val elements: List<ElementConfig>,
) : ContainerConfig {
    override val containerType: ContainerType = ContainerType.SECTION
}