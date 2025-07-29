package com.github.openflocon.flocon.myapplication.table

import android.content.Context
import com.github.openflocon.flocon.Flocon
import com.github.openflocon.flocon.plugins.tables.model.toParam
import com.github.openflocon.flocon.plugins.tables.table

fun initializeTable(context: Context) {
    Flocon.table("analytics").log(
        "name" toParam "nameValue",
        "value1" toParam "value1Value",
        "value2" toParam "value2Value",
    )
}