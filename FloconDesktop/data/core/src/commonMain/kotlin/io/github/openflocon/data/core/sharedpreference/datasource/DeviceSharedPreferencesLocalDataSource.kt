package io.github.openflocon.data.core.sharedpreference.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import kotlinx.coroutines.flow.Flow

interface DeviceSharedPreferencesLocalDataSource {

    fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?>

    fun selectDeviceSharedPreference(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceSharedPreferenceDomainModel>>

    fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    )
}
