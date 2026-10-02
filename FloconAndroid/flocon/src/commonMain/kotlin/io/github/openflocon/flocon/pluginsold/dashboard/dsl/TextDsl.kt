package io.github.openflocon.flocon.pluginsold.dashboard.dsl

import io.github.openflocon.flocon.pluginsold.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.pluginsold.dashboard.model.config.LabelConfig
import io.github.openflocon.flocon.pluginsold.dashboard.model.config.TextConfig

@DashboardDsl
fun ContainerBuilder.text(label: String, value: String, color: Int? = null) {
    add(TextConfig(label = label, value = value, color = color))
}

@DashboardDsl
fun ContainerBuilder.label(label: String, color: Int? = null) {
    add(LabelConfig(label = label, color = color))
}