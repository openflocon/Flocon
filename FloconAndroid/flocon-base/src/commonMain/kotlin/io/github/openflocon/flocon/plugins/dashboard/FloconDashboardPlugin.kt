package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig

interface FloconDashboardPlugin {
    fun registerDashboard(dashboardConfig: DashboardConfig)
}
