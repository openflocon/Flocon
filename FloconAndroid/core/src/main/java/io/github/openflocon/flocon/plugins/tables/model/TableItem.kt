package io.github.openflocon.flocon.plugins.tables.model

import org.json.JSONArray
import org.json.JSONObject

data class TableItem(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnConfig>,
) {
    companion object {
        fun listToJson(items: Collection<TableItem>) : JSONArray {
            val array = JSONArray()

            items.forEach {
                array.put(it.toJson())
            }

            return array
        }
    }
}

private fun TableItem.toJson() : JSONObject {
    val tableItemJson = JSONObject()

    tableItemJson.put("id", this.id)
    tableItemJson.put("name", this.name)
    tableItemJson.put("createdAt", this.createdAt)

    val array = JSONArray()
    columns.forEach {
        array.put(
            JSONObject().apply {
                put("column", it.columnName)
                put("value", it.value)
            }
        )
    }

    tableItemJson.put("columns", array)
    return tableItemJson
}