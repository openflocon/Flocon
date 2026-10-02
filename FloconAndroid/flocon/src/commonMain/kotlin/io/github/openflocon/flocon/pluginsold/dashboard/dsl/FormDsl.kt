package io.github.openflocon.flocon.pluginsold.dashboard.dsl

import io.github.openflocon.flocon.pluginsold.dashboard.builder.DashboardBuilder
import io.github.openflocon.flocon.pluginsold.dashboard.builder.FormBuilder

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