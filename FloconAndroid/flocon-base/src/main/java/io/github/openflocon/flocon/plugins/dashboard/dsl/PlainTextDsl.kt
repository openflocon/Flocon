package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.model.config.PlainTextConfig
import io.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder

@DashboardDsl
fun SectionBuilder.plainText(label: String, value: String) {
    add(
        PlainTextConfig(
            label = label,
            value = value,
            type = "text",
        )
    )
}

@DashboardDsl
fun SectionBuilder.json(label: String, value: String) {
    add(PlainTextConfig(
        label = label,
        value = value,
        type = "json",
    ))
}