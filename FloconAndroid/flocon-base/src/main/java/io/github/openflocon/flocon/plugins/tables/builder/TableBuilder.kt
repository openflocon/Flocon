package io.github.openflocon.flocon.plugins.tables.builder

import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import io.github.openflocon.flocon.plugins.tables.model.TableColumnConfig
import io.github.openflocon.flocon.plugins.tables.model.TableItem
import java.util.UUID

class TableBuilder(
    val tableName: String,
    private val tablePlugin: FloconTablePlugin?,
) {
    fun log(vararg columns: TableColumnConfig) {
        val dashboardConfig = TableItem(
            id = UUID.randomUUID().toString(),
            name = tableName,
            columns = columns.toList(),
            createdAt = System.currentTimeMillis(),
        )
        tablePlugin?.registerTable(dashboardConfig)
    }
}