package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig

class FloconDashboardConfig

/**
 * Flocon Dashboard Plugin.
 */
expect object FloconDashboard : FloconPluginFactory<FloconDashboardConfig, FloconDashboardPlugin>

interface FloconDashboardPlugin : FloconPlugin {
    fun registerDashboard(dashboardConfig: DashboardConfig)
}
