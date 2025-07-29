package com.github.openflocon.flocon.plugins.dashboard.dsl

import com.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder
import com.github.openflocon.flocon.plugins.dashboard.model.config.TextFieldConfig

@DashboardDsl
fun SectionBuilder.textField(
    id: String,
    label: String,
    placeHolder: String?,
    value: String,
    onSubmitted: (String) -> Unit,
) {
    add(
        TextFieldConfig(
            id = id,
            label = label,
            placeHolder = placeHolder,
            value = value,
            onSubmitted = onSubmitted,
        )
    )
}
