package com.florent37.flocondesktop.features.sharedpreferences.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceValuesResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private data class SharedPrefKeyForDevice(
    val deviceId: DeviceId,
    val sharedPreferenceId: DeviceSharedPreferenceId,
)

class DeviceSharedPreferencesValuesDataSource {
    private val sharedPreferencesValues =
        MutableStateFlow<Map<SharedPrefKeyForDevice, List<SharedPreferenceRowDomainModel>>>(emptyMap())

    fun onSharedPreferencesValuesReceived(
        deviceId: DeviceId,
        received: SharedPreferenceValuesResponseDomainModel,
    ) {
        val key =
            SharedPrefKeyForDevice(
                deviceId = deviceId,
                sharedPreferenceId = received.sharedPreferenceName,
            )
        sharedPreferencesValues.update {
            val row = key to received.rows
            it + row
        }
    }

    fun observe(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>> {
        val key =
            SharedPrefKeyForDevice(
                deviceId = deviceId,
                sharedPreferenceId = sharedPreferenceId,
            )
        return sharedPreferencesValues
            .map {
                it[key] ?: emptyList()
            }.distinctUntilChanged()
    }
}
