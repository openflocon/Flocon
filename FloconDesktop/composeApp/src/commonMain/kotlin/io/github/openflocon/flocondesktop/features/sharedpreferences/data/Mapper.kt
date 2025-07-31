package io.github.openflocon.flocondesktop.features.sharedpreferences.data

import com.florent37.flocondesktop.features.sharedpreferences.data.model.incoming.DeviceSharedPreferenceDataModel
import com.florent37.flocondesktop.features.sharedpreferences.data.model.incoming.SharedPreferenceValuesResponse
import kotlinx.serialization.json.Json

// maybe inject
private val sharedPreferencesJsonParser =
    Json {
        ignoreUnknownKeys = true
    }

internal fun decodeDeviceSharedPreferences(body: String): List<DeviceSharedPreferenceDataModel>? = try {
    sharedPreferencesJsonParser.decodeFromString<List<DeviceSharedPreferenceDataModel>>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

internal fun decodeSharedPreferenceValuesResponse(body: String): SharedPreferenceValuesResponse? = try {
    sharedPreferencesJsonParser.decodeFromString<SharedPreferenceValuesResponse>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}
