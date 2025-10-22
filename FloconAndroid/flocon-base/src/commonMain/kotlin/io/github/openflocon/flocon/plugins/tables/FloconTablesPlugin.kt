package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.tables.builder.TableBuilder
import io.github.openflocon.flocon.plugins.tables.model.TableItem

fun floconTable(tableName: String): TableBuilder {
    return TableBuilder(
        tableName = tableName,
        tablePlugin = FloconApp.instance?.client?.tablePlugin,
    )
}

fun FloconApp.table(tableName: String): TableBuilder {
    return TableBuilder(
        tableName = tableName,
        tablePlugin = this.client?.tablePlugin,
    )
}

interface FloconTablePlugin {
    fun registerTable(tableItem: TableItem)
}