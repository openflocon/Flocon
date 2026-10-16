package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.MarkdownConfig

@DashboardDsl
fun ContainerBuilder.markdown(label: String, value: String) {
    add(MarkdownConfig(label = label, value = value))
}
