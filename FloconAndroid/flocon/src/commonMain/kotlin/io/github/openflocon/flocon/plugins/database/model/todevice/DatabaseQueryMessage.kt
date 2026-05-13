package io.github.openflocon.flocon.plugins.database.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseQueryMessage(
    val query: String,
    val requestId: String,
    val database: String,
)