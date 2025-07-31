package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
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
        deviceId: com.florent37.flocondesktop.DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>>

    suspend fun editSharedPrefField(
        deviceId: DeviceId,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    )
}
