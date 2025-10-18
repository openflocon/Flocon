package io.github.openflocon.flocon.plugins.database.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class DeviceDataBaseDataModel(
    val id: String,
    val name: String,
)

internal fun listDeviceDataBaseDataModelToJson(items: List<DeviceDataBaseDataModel>) : String {
    return FloconEncoder.json.encodeToString(items)
}