package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {
    fun observeDeviceSharedPreferences(deviceId: DeviceId): Flow<List<DeviceSharedPreferenceDomainModel>>

    fun observeSelectedDeviceSharedPreference(deviceId: DeviceId): Flow<DeviceSharedPreferenceDomainModel?>

    fun selectDeviceSharedPreference(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    suspend fun registerDeviceSharedPreferences(
        deviceId: DeviceId,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    )

    suspend fun askForDeviceSharedPreferences(deviceId: DeviceId)

    suspend fun getDeviceSharedPreferencesValues(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    fun observe(
        deviceId: io.github.openflocon.flocondesktop.DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>>

    suspend fun editSharedPrefField(
        deviceId: DeviceId,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    )
}
