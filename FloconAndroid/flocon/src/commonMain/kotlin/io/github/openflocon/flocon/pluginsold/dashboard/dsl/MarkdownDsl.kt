package io.github.openflocon.flocon.pluginsold.dashboard.dsl

import io.github.openflocon.flocon.pluginsold.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.pluginsold.dashboard.model.config.MarkdownConfig

@DashboardDsl
fun ContainerBuilder.markdown(label: String, value: String) {
    add(MarkdownConfig(label = label, value = value))
}
