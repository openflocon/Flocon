package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.builder.DashboardBuilder
import io.github.openflocon.flocon.plugins.dashboard.builder.SectionBuilder

@DashboardDsl
fun DashboardBuilder.section(name: String, block: SectionBuilder.() -> Unit) {
    val builder = SectionBuilder(name).apply {
        block()
    }

    add(builder.build())
}