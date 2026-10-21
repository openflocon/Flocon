package io.github.openflocon.flocon.tables

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.tables.model.TableItem

class FloconTableConfig : FloconPluginConfig

interface FloconTablePlugin : FloconPlugin {
    fun registerItems(tableItems: List<TableItem>)
}

object FloconTable : FloconPluginFactory<FloconTableConfig, FloconTablePlugin> {
    override val name: String = "Table"
    override val pluginId: String = "Table"
    override fun createConfig(context: FloconContext) = FloconTableConfig()
    override fun install(
        pluginConfig: FloconTableConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconTablePlugin = FloconTablePluginNoOp
}

internal object FloconTablePluginNoOp : FloconTablePlugin {
    override val key: String = "Table"
    override fun registerItems(tableItems: List<TableItem>) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.tablePlugin: FloconTablePlugin
    get() = FloconTablePluginNoOp
