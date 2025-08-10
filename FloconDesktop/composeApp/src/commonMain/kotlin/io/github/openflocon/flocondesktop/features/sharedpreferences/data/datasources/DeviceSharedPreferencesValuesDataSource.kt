package io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources

import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceValuesResponseDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private data class SharedPrefKeyForDevice(
    val deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    val sharedPreferenceId: DeviceSharedPreferenceId,
)

class DeviceSharedPreferencesValuesDataSource {
    private val sharedPreferencesValues =
        MutableStateFlow<Map<SharedPrefKeyForDevice, List<SharedPreferenceRowDomainModel>>>(emptyMap())

    fun onSharedPreferencesValuesReceived(
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

    fun observe(
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
