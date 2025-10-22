package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.PlainTextConfig

@DashboardDsl
fun ContainerBuilder.plainText(label: String, value: String) {
    add(
        PlainTextConfig(
            label = label,
            value = value,
            type = "text",
        )
    )
}

@DashboardDsl
fun ContainerBuilder.json(label: String, value: String) {
    add(PlainTextConfig(
        label = label,
        value = value,
        type = "json",
    ))
}