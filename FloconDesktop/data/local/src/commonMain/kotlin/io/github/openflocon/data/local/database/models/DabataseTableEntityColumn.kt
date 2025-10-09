package io.github.openflocon.data.local.database.models

import kotlinx.serialization.Serializable

@Serializable
data class DabataseTableEntityColumn(
    val name: String,
    val type: String,
    val nullable: Boolean,
    val primaryKey: Boolean,
)
