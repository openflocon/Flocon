package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig

@DslMarker
annotation class DashboardDsl

fun dashboardConfig(id: String, block: DashboardBuilder.() -> Unit): DashboardConfig {
    val builder = DashboardBuilder(id = id)
        .apply {
            block()
        }
    return builder.build()
}