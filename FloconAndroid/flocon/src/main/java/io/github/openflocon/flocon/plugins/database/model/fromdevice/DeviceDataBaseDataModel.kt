package io.github.openflocon.flocon.plugins.database.model.fromdevice

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal data class DeviceDataBaseDataModel(
    val id: String,
    val name: String,
)

internal fun listDeviceDataBaseDataModelToJson(items: List<DeviceDataBaseDataModel>, json: Json) : String {
    return json.encodeToString(items)
}