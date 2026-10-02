@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.pluginsold.tables.builder

import kotlin.uuid.ExperimentalUuidApi

//class TableBuilder(
//    val tableName: String,
//    private val tablePlugin: FloconTablePlugin?,
//) {
//    fun log(vararg columns: TableColumnConfig) {
//        val dashboardConfig = TableItem(
//            id = Uuid.random().toString(),
//            name = tableName,
//            columns = columns.toList(),
//            createdAt = currentTimeMillis(),
//        )
//        tablePlugin?.registerTable(dashboardConfig)
//    }
//}