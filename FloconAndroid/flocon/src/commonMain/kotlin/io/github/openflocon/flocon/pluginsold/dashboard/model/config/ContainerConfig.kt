package io.github.openflocon.flocon.pluginsold.dashboard.model.config

import io.github.openflocon.flocon.pluginsold.dashboard.model.ContainerType

sealed interface ContainerConfig {
    val name: String
    val elements: List<ElementConfig>
    val containerType: ContainerType
}