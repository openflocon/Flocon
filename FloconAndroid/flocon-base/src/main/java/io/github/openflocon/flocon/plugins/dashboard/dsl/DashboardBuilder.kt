package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig

@DashboardDsl
class DashboardBuilder(private val id: String) {
    private val sections = mutableListOf<SectionConfig>()

    fun add(section: SectionConfig) {
        sections.add(section)
    }

    fun build(): DashboardConfig {
        return DashboardConfig(
            id = id,
            sections = sections
        )
    }

}