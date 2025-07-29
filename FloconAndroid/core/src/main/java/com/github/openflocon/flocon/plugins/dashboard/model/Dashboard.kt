package com.github.openflocon.flocon.plugins.dashboard.model

import com.github.openflocon.flocon.plugins.dashboard.model.config.SectionConfig

data class DashboardConfig(
    val id: String,
    val sections: List<SectionConfig>
)