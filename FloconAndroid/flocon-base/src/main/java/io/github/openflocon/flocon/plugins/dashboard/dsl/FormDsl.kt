package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.DashboardBuilder
import io.github.openflocon.flocon.plugins.dashboard.builder.FormBuilder

@DashboardDsl
fun DashboardBuilder.form(
    name: String,
    submitText: String,
    onSubmitted: (Map<String, String>) -> Unit,
    block: FormBuilder.() -> Unit
) {
    val builder = FormBuilder(
        name = name,
        submitText = submitText,
        onSubmitted = onSubmitted
    ).apply {
        block()
    }

    add(builder.build())
}