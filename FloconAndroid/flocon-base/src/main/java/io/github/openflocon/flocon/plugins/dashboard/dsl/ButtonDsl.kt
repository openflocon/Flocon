package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.ButtonConfig

@DashboardDsl
fun ContainerBuilder.button(
    text: String,
    id : String,
    onClick: () -> Unit,
) {
    add(
        ButtonConfig(
            text = text,
            id = id,
            onClick = onClick,
        )
    )
}