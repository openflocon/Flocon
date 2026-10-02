package io.github.openflocon.flocon.pluginsold.dashboard

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.pluginsold.dashboard.model.DashboardConfig

class FloconDashboardConfig : FloconPluginConfig

/**
 * Flocon Dashboard Plugin.
 */
//expect object FloconDashboard : FloconPluginFactory<FloconDashboardConfig, FloconDashboardPlugin>
//
interface FloconDashboardPlugin : FloconPlugin {
    fun registerDashboard(dashboardConfig: DashboardConfig)
}
