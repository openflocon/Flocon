package com.flocon.data.remote.sharedpreference.mapper

import com.flocon.data.remote.sharedpreference.models.DeviceSharedPreferenceDataModel
import kotlinx.serialization.json.Json

// maybe inject
private val sharedPreferencesJsonParser = Json {
    ignoreUnknownKeys = true
}

// TODO Internal
fun decodeDeviceSharedPreferences(body: String): List<DeviceSharedPreferenceDataModel>? = try {
    sharedPreferencesJsonParser.decodeFromString<List<DeviceSharedPreferenceDataModel>>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

fun decodeSharedPreferenceValuesResponse(body: String): SharedPreferenceValuesResponse? = try {
    sharedPreferencesJsonParser.decodeFromString<SharedPreferenceValuesResponse>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}
