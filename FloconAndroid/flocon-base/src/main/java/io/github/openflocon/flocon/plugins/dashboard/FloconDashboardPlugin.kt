package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.dashboard.builder.DashboardBuilder
import io.github.openflocon.flocon.plugins.dashboard.dsl.dashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig


fun FloconApp.dashboard(id: String, block: DashboardBuilder.() -> Unit) {
    this.client?.dashboardPlugin?.let {
        // on no op, this callback is never called
        val dashboardConfig = dashboardConfig(id = id, block)
        it.registerDashboard(dashboardConfig)
    }
}

interface FloconDashboardPlugin : FloconPlugin {
    fun registerDashboard(dashboardConfig: DashboardConfig)
}