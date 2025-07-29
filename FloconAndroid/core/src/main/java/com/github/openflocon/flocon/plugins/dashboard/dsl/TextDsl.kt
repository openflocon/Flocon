package com.github.openflocon.flocon.plugins.dashboard.dsl

import com.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder
import com.github.openflocon.flocon.plugins.dashboard.model.config.TextConfig

@DashboardDsl
fun SectionBuilder.text(label: String, value: String, color: Int? = null) {
    add(TextConfig(label = label, value = value, color = color))
}