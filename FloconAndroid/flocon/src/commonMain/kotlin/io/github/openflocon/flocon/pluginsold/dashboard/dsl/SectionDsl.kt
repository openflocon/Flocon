package io.github.openflocon.flocon.pluginsold.dashboard.dsl

import io.github.openflocon.flocon.pluginsold.dashboard.builder.DashboardBuilder
import io.github.openflocon.flocon.pluginsold.dashboard.builder.SectionBuilder

@DashboardDsl
fun DashboardBuilder.section(name: String, block: SectionBuilder.() -> Unit) {
    val builder = SectionBuilder(name).apply {
        block()
    }

    add(builder.build())
}