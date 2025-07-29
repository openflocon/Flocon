package com.github.openflocon.flocon.plugins.dashboard.dsl

import com.github.openflocon.flocon.plugins.dashboard.model.config.SectionBuilder

@DashboardDsl
fun DashboardBuilder.section(name: String, block: SectionBuilder.() -> Unit) {
    val builder = SectionBuilder(name).apply {
        block()
    }
    add(builder.build())
}