@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package io.github.openflocon.flocon.plugins.tables.dsl

import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import io.github.openflocon.flocon.plugins.tables.model.TableColumnConfig
import io.github.openflocon.flocon.plugins.tables.model.TableItem
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun FloconTablePlugin.table(tableName: String, block: TableItemDefinition.() -> Unit = {}) {
    val item = TableItemDefinition(tableName).apply(block)
        .build()

    registerItems(tableItems = listOf(item))
}

class TableItemDefinition internal constructor(private val name: String) {

    private val columns: MutableList<TableColumnConfig> = mutableListOf()

    fun column(name: String, value: String) {
        columns.add(TableColumnConfig(columnName = name, value = value))
    }

    internal fun build() = TableItem(
        id = Uuid.random().toHexString(),
        name = name,
        createdAt =  Clock.System.now().toEpochMilliseconds(),
        columns = emptyList()
    )

}