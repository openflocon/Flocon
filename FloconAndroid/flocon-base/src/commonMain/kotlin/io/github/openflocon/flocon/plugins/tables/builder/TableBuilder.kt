package io.github.openflocon.flocon.plugins.tables.builder

import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import io.github.openflocon.flocon.plugins.tables.model.TableColumnConfig
import io.github.openflocon.flocon.plugins.tables.model.TableItem
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocon.utils.generateUuid

class TableBuilder(
    val tableName: String,
    private val tablePlugin: FloconTablePlugin?,
) {
    fun log(vararg columns: TableColumnConfig) {
        val dashboardConfig = TableItem(
            id = generateUuid(),
            name = tableName,
            columns = columns.toList(),
            createdAt = currentTimeMillis(),
        )
        tablePlugin?.registerTable(dashboardConfig)
    }
}