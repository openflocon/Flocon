package io.github.openflocon.flocon.plugins.dashboard.model.config

import io.github.openflocon.flocon.plugins.dashboard.model.ContainerType

sealed interface ContainerConfig {
    val name: String
    val elements: List<ElementConfig>
    val containerType: ContainerType
}