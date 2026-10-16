package io.github.openflocon.flocon.pluginsold.dashboard.dsl

import io.github.openflocon.flocon.pluginsold.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.pluginsold.dashboard.model.config.CheckBoxConfig

@DashboardDsl
fun ContainerBuilder.checkBox(
    id: String,
    label: String,
    value: Boolean,
    onUpdated: (Boolean) -> Unit = {},
) {
    add(
        CheckBoxConfig(
            id = id,
            label = label,
            value = value,
            onUpdated = onUpdated,
        )
    )
}
