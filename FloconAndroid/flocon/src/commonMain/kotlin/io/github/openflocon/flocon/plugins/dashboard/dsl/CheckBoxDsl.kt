package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.ContainerBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.config.CheckBoxConfig

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
