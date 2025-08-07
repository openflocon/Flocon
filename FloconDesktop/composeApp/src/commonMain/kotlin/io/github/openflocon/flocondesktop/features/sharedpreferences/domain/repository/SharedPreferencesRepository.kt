package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository

import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {
    fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<DeviceSharedPreferenceDomainModel>>

    fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DeviceSharedPreferenceDomainModel?>

    fun selectDeviceSharedPreference(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    suspend fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    )

    suspend fun askForDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageName)

    suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>>

    suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    )
}
