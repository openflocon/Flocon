package com.flocon.data.remote.table.model

import kotlinx.serialization.Serializable

@Serializable
data class TableItemDataModel(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnDataModel>,
)
