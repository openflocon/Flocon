package io.github.openflocon.flocon.tables.dsl

import io.github.openflocon.flocon.tables.FloconTablePlugin

fun FloconTablePlugin.table(tableName: String, block: TableItemDefinition.() -> Unit = {}) {
    // no op
}

class TableItemDefinition internal constructor(private val name: String) {
    fun column(name: String, value: String) {
        // no op
    }
}
