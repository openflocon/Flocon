@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.plugins.tables.model

import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TableBuilder(
    val tableName: String,
    private val tablePlugin: FloconTablePlugin?,
) {
    fun log(vararg columns: TableColumnConfig) {
        val dashboardConfig = TableItem(
            id = Uuid.random().toString(),
            name = tableName,
            columns = columns.toList(),
            createdAt = currentTimeMillis(),
        )

        tablePlugin?.registerItems(listOf(dashboardConfig))
    }
}