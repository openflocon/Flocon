package io.github.openflocon.flocon.plugins.database.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class DeviceDataBaseDataModel(
    val id: String,
    val name: String,
)