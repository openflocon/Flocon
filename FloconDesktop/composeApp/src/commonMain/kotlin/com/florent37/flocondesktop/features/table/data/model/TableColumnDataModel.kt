package com.florent37.flocondesktop.features.table.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TableColumnDataModel(
    val column: String,
    val value: String,
)
