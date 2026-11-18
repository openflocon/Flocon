package io.github.openflocon.data.local.sharedpreference.datasources

import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceValuesResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private data class SharedPrefKeyForDevice(
    val deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    val sharedPreferenceId: DeviceSharedPreferenceId,
)

class DeviceSharedPreferencesValuesDataSourceImpl : DeviceSharedPreferencesValuesDataSource {
    private val sharedPreferencesValues =
        MutableStateFlow<Map<SharedPrefKeyForDevice, List<SharedPreferenceRowDomainModel>>>(emptyMap())

    override fun onSharedPreferencesValuesReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        received: SharedPreferenceValuesResponseDomainModel,
    ) {
        val key = SharedPrefKeyForDevice(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = received.sharedPreferenceName,
        )

        sharedPreferencesValues.update {
            val row = key to received.rows
            it + row
        }
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>> {
        val key = SharedPrefKeyForDevice(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = sharedPreferenceId,
        )

        return sharedPreferencesValues
            .map { it[key] ?: emptyList() }
            .distinctUntilChanged()
    }
}
