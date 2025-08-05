package io.github.openflocon.flocon.plugins.dashboard.model.config

class SectionBuilder(val name: String) {
    private val elements = mutableListOf<ElementConfig>()

    fun add(element: ElementConfig) {
        elements.add(element)
    }

    fun build(): SectionConfig {
        return SectionConfig(name, elements)
    }
}