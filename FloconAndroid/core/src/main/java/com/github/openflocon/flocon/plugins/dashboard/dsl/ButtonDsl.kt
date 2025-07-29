package com.github.openflocon.flocon.plugins.dashboard.dsl

import com.github.openflocon.flocon.plugins.dashboard.model.config.ButtonConfig
import com.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder

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