package io.github.openflocon.domain.sharedpreference.models

typealias DeviceSharedPreferenceId = String

data class DeviceSharedPreferenceDomainModel(
    val id: DeviceSharedPreferenceId,
    val name: String,
    // storage sent by the app (SharedPreferences, DataStore, UserDefaults...), null for older SDKs
    val type: String? = null,
)
