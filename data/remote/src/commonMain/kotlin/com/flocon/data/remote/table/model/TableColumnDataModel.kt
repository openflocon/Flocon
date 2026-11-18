package com.flocon.data.remote.table.model

import kotlinx.serialization.Serializable

@Serializable
data class TableColumnDataModel(
    val column: String,
    val value: String,
)
