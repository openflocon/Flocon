package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.TextConfig

@DashboardDsl
fun ContainerBuilder.text(label: String, value: String, color: Int? = null) {
    add(TextConfig(label = label, value = value, color = color))
}