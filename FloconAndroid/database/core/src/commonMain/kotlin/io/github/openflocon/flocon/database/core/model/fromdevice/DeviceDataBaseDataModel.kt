package io.github.openflocon.flocon.database.core.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class DeviceDataBaseDataModel(
    val id: String,
    val name: String,
)

fun listDeviceDataBaseDataModelToJson(items: List<DeviceDataBaseDataModel>) : String {
    return FloconEncoder.json.encodeToString(items)
}
