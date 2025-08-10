package io.github.openflocon.flocon.plugins.tables

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.tables.builder.TableBuilder
import io.github.openflocon.flocon.plugins.tables.model.TableItem

fun FloconApp.table(tableName: String): TableBuilder {
    return TableBuilder(
        tableName = tableName,
        tablePlugin = this.client?.tablePlugin,
    )
}

interface FloconTablePlugin : FloconPlugin {
    fun registerTable(tableItem: TableItem)
}