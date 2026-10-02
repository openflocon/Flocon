package io.github.openflocon.flocon.database.core.model.fromdevice.sql

import kotlinx.serialization.Serializable

@Serializable
data class DeviceDataBaseDataModel(
    val id: String,
    val name: String
)
