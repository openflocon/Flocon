package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.pluginsold.tables.FloconTableConfig
import io.github.openflocon.flocon.pluginsold.tables.FloconTablePlugin
import io.github.openflocon.flocon.pluginsold.tables.model.TableItem

 object FloconTable : FloconPluginFactory<FloconTableConfig, FloconTablePlugin> {
    override val name: String = "Table"
    override val pluginId: String = Protocol.ToDevice.Table.Plugin
    override fun createConfig() = FloconTableConfig()
    override fun install(
        pluginConfig: FloconTableConfig,
        floconConfig: FloconConfig
    ): FloconTablePlugin {
        return FloconTablePluginImpl(
            sender = floconConfig.client as FloconMessageSender
        )
    }
}

internal class FloconTablePluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconTablePlugin {
    override val key: String = "TABLE"

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    override fun registerItems(tableItems: List<TableItem>) {
        sendTable(tableItems)
    }

    private fun sendTable(tableItems: List<TableItem>) {
        try {
//            sender.send(
//                plugin = Protocol.FromDevice.Table.Plugin,
//                method = Protocol.FromDevice.Table.Method.AddItems,
//                body = tableItemListToJson(tableItems).toString()
//            )
        } catch (t: Throwable) {
            FloconLogger.logError("Table json mapping error", t)
        }
    }
}