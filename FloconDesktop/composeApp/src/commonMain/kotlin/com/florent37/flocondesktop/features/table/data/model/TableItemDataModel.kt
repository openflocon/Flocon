package com.florent37.flocondesktop.features.table.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TableItemDataModel(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnDataModel>,
)
