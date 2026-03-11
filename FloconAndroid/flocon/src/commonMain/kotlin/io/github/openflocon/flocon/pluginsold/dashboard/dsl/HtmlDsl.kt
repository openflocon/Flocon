package io.github.openflocon.flocon.pluginsold.dashboard.dsl

import io.github.openflocon.flocon.pluginsold.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.pluginsold.dashboard.model.config.HtmlConfig

@DashboardDsl
fun ContainerBuilder.html(label: String, value: String) {
    add(HtmlConfig(label = label, value = value))
}
