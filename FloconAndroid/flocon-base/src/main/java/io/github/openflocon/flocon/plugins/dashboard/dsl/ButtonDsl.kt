package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.model.config.ButtonConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder

@DashboardDsl
fun SectionBuilder.button(
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