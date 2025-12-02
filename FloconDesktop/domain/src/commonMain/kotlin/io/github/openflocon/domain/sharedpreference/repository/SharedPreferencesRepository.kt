package io.github.openflocon.domain.sharedpreference.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
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
