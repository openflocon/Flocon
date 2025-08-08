package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository

import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {
    fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceSharedPreferenceDomainModel>>

    fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?>

    fun selectDeviceSharedPreference(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    suspend fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    )

    suspend fun askForDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>>

    suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    )
}
