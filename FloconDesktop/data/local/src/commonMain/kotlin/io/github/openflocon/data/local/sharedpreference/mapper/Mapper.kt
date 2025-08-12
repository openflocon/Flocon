package io.github.openflocon.data.local.sharedpreference.mapper

import io.github.openflocon.data.local.sharedpreference.model.DeviceSharedPreferenceDataModel
import io.github.openflocon.data.local.sharedpreference.model.SharedPreferenceValuesResponse
import kotlinx.serialization.json.Json

// maybe inject
private val sharedPreferencesJsonParser =
    Json {
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
