package io.github.openflocon.flocon.plugins.dashboard.model

import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig

data class DashboardConfig(
    val id: String,
    val sections: List<SectionConfig>
)