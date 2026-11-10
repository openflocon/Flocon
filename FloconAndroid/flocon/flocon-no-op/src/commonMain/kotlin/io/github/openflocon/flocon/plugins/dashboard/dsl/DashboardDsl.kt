package io.github.openflocon.flocon.plugins.dashboard.dsl

import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPlugin
import io.github.openflocon.flocon.plugins.dashboard.builder.DashboardBuilder

@DslMarker
annotation class DashboardDsl

/**
 * Registers a static dashboard, see [io.github.openflocon.flocon.plugins.dashboard.dashboard] for a reactive one.
 */
fun FloconDashboardPlugin.registerDashboard(id: String, block: DashboardBuilder.() -> Unit) {
    registerDashboard(
        DashboardBuilder(id = id)
            .apply(block)
            .build()
    )
}
