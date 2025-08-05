package io.github.openflocon.flocondesktop.features.table.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TableItemDataModel(
    val id: String,
    val name: String,
    val createdAt: Long,
    val columns: List<TableColumnDataModel>,
)
