package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.tables.model.TableItem

interface FloconTablePlugin : FloconPlugin {
    fun registerItems(tableItems: List<TableItem>)
}

class FloconTableConfig internal constructor() : FloconPluginConfig

object FloconTable : FloconPluginFactory<FloconTableConfig, FloconTablePlugin> {
    override val name: String = "Table"
    override val pluginId: String = Protocol.ToDevice.Table.Plugin
    override fun createConfig(context: FloconContext) = FloconTableConfig()
    override fun install(
        pluginConfig: FloconTableConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconTablePlugin {
        return FloconTablePluginNoOp
    }
}

private object FloconTablePluginNoOp : FloconTablePlugin {
    override val key: String = "TABLE"

    override suspend fun onMessageReceived(method: String, body: String) {
        // no-op
    }

    override suspend fun onConnectedToServer() {
        // no-op
    }

    override fun registerItems(tableItems: List<TableItem>) {
        // no-op
    }
}
