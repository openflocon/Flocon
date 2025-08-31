package io.github.openflocon.flocon.plugins.dashboard.builder

import io.github.openflocon.flocon.plugins.dashboard.model.config.ContainerConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.ElementConfig

abstract class ContainerBuilder {
    open val elements = mutableListOf<ElementConfig>()

    open fun add(element: ElementConfig) {
        elements.add(element)
    }

    abstract fun build(): ContainerConfig
}