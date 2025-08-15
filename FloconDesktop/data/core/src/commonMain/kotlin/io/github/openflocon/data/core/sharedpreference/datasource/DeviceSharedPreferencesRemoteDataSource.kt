package io.github.openflocon.data.core.sharedpreference.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceValuesResponseDomainModel

interface DeviceSharedPreferencesRemoteDataSource {

    suspend fun askForDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    )

    suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    )

    suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    )

    fun getPreferences(message: FloconIncomingMessageDomainModel): List<DeviceSharedPreferenceDomainModel>

    fun getValues(message: FloconIncomingMessageDomainModel): SharedPreferenceValuesResponseDomainModel?

}
