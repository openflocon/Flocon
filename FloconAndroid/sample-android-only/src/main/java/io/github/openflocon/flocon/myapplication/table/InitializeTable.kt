package io.github.openflocon.flocon.myapplication.table

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.tables.dsl.table
import io.github.openflocon.flocon.plugins.tables.tablePlugin

fun initializeTable() {
    Flocon.tablePlugin.table("analytics") {
        column("name", "nameValue")
        column("value1", "value1Value")
        column("value2", "value2Value")
    }
}