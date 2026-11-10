package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.HtmlConfig

@DashboardDsl
fun ContainerBuilder.html(label: String, value: String) {
    add(HtmlConfig(label = label, value = value))
}
