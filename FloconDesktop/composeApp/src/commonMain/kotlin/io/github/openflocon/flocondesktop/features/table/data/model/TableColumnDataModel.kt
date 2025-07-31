package io.github.openflocon.flocondesktop.features.table.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TableColumnDataModel(
    val column: String,
    val value: String,
)
