package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.LabelConfig

@DashboardDsl
fun ContainerBuilder.text(label: String, value: String, color: Int? = null) {
    add(TextConfig(label = label, value = value, color = color))
}

@DashboardDsl
fun ContainerBuilder.label(label: String, color: Int? = null) {
    add(LabelConfig(label = label, color = color))
}