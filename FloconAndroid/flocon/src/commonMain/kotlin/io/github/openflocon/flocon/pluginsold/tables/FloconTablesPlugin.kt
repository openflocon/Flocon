package io.github.openflocon.flocon.pluginsold.tables

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.pluginsold.tables.model.TableItem

class FloconTableConfig

/**
 * Flocon Table Plugin.
 * Used to display custom data tables.
 */
object FloconTable : FloconPluginFactory<FloconTableConfig, FloconTablePlugin> {
    override fun createConfig(): FloconTableConfig {
        TODO("Not yet implemented")
    }

    override fun install(config: Any, app: FloconApp): FloconTablePlugin {
        TODO("Not yet implemented")
    }

    override val name: String
        get() = TODO("Not yet implemented")
}

//fun floconTable(tableName: String) : TableBuilder {
//    return TableBuilder(
//        tableId = tableName,
//        tablePlugin = FloconApp.instance?.client?.tablePlugin,
//    )
//}
//
//fun FloconApp.table(tableName: String): TableBuilder {
//    return TableBuilder(
//        tableId = tableName,
//        tablePlugin = this.client?.tablePlugin,
//    )
//}

interface FloconTablePlugin : FloconPlugin {
    fun registerItems(tableItems: List<TableItem>)
}