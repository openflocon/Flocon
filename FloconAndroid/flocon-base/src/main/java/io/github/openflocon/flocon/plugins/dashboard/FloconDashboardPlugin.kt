package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.dashboard.dsl.DashboardBuilder
import io.github.openflocon.flocon.plugins.dashboard.dsl.dashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig


fun FloconApp.dashboard(id: String, block: DashboardBuilder.() -> Unit) {
    val dashboardConfig = dashboardConfig(id = id, block)
    this.client?.dashboardPlugin?.registerDashboard(dashboardConfig)
}

interface FloconDashboardPlugin : FloconPlugin {
    fun registerDashboard(dashboardConfig: DashboardConfig)
}