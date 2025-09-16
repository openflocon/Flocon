package io.github.openflocon.flocon.plugins.database.model.fromdevice

import org.json.JSONArray
import org.json.JSONObject

internal data class DeviceDataBaseDataModel(
    val id: String,
    val name: String,
) {
    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("id", id)
        json.put("name", name)

        return json
    }
}

internal fun listDeviceDataBaseDataModelToJson(items: List<DeviceDataBaseDataModel>) : JSONArray {
    val array = JSONArray()
    items.forEach {
        array.put(it.toJson())
    }
    return array
}