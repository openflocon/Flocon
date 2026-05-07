package io.github.openflocon.flocon.myapplication.table

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.tables.table
import io.github.openflocon.flocon.plugins.tables.tablePlugin

fun initializeTable() {
    Flocon.tablePlugin.table("analytics")
//        .log(
//        "name" toParam "nameValue",
//        "value1" toParam "value1Value",
//        "value2" toParam "value2Value",
//    )
}