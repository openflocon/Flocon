package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig

class FloconDashboardConfig : FloconPluginConfig

interface FloconDashboardPlugin : FloconPlugin {
    fun registerDashboard(dashboardConfig: DashboardConfig)
}

object FloconDashboard : FloconPluginFactory<FloconDashboardConfig, FloconDashboardPlugin> {
    override val name: String = "Dashboard"
    override val pluginId: String = "Dashboard"
    override fun createConfig(context: FloconContext) = FloconDashboardConfig()
    override fun install(
        pluginConfig: FloconDashboardConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconDashboardPlugin = FloconDashboardPluginNoOp
}

internal object FloconDashboardPluginNoOp : FloconDashboardPlugin {
    override val key: String = "Dashboard"
    override fun registerDashboard(dashboardConfig: DashboardConfig) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.dashboardPlugin: FloconDashboardPlugin
    get() = FloconDashboardPluginNoOp
