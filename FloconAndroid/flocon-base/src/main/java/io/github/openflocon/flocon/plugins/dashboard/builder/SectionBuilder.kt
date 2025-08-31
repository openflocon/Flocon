package io.github.openflocon.flocon.plugins.dashboard.builder

import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig

class SectionBuilder(val name: String) : ContainerBuilder() {

    override fun build(): SectionConfig {
        return SectionConfig(name, elements)
    }
}