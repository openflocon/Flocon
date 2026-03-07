package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.plugins.tables.builder.TableBuilder
import io.github.openflocon.flocon.plugins.tables.model.TableItem

class FloconTableConfig

/**
 * Flocon Table Plugin.
 * Used to display custom data tables.
 */
expect object FloconTable : FloconPluginFactory<FloconTableConfig, FloconTablePlugin>

fun floconTable(tableName: String) : TableBuilder {
    return TableBuilder(
        tableId = tableName,
        tablePlugin = FloconApp.instance?.client?.tablePlugin,
    )
}

fun FloconApp.table(tableName: String): TableBuilder {
    return TableBuilder(
        tableId = tableName,
        tablePlugin = this.client?.tablePlugin,
    )
}

interface FloconTablePlugin : FloconPlugin {
    fun registerItems(tableItems: List<TableItem>)
}