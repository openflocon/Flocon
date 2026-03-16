package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.plugins.tables.model.TableItem

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object FloconTable : FloconPluginFactory<FloconTableConfig, FloconTablePlugin> {
    override val name: String = "Table"
    override val pluginId: String? = null
    override fun createConfig(context: FloconContext) = FloconTableConfig()
    override fun install(
        pluginConfig: FloconTableConfig,
        floconConfig: FloconConfig
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
