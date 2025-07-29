package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.model.config.CheckBoxConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder

@DashboardDsl
fun SectionBuilder.checkBox(
    id: String,
    label: String,
    value: Boolean,
    onUpdated: (Boolean) -> Unit,
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
