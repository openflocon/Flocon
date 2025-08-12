package io.github.openflocon.data.local.sharedpreference.datasources

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceSharedPreferencesDataSource {
    private val deviceSharedPreferences =
        MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, List<DeviceSharedPreferenceDomainModel>>>(emptyMap())

    private val selectedDeviceSharedPreferences =
        MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, DeviceSharedPreferenceDomainModel?>>(emptyMap())

    fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?> =
        selectedDeviceSharedPreferences
            .map { it[deviceIdAndPackageName] }
            .distinctUntilChanged()

    fun selectDeviceSharedPreference(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        val deviceSharedPreferenceList = deviceSharedPreferences.value[deviceIdAndPackageName] ?: return
        val sharedPreference =
            deviceSharedPreferenceList.firstOrNull { it.id == sharedPreferenceId } ?: return

        selectedDeviceSharedPreferences.update {
            it + (deviceIdAndPackageName to sharedPreference)
        }
    }

    fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceSharedPreferenceDomainModel>> =
        deviceSharedPreferences.map { it[deviceIdAndPackageName] ?: emptyList() }

    fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    ) {
        deviceSharedPreferences.update {
            val actual = it[deviceIdAndPackageName]
            val newList = buildList {
                actual?.let(::addAll)
                addAll(sharedPreferences)
            }
                .distinct()
            it + (deviceIdAndPackageName to newList)
        }

        if (sharedPreferences.isNotEmpty()) {
            // select the first db if no one for this device id
            selectedDeviceSharedPreferences.update { state ->
                val dbForThisDevice = state[deviceIdAndPackageName]
                if (dbForThisDevice == null) {
                    state + (deviceIdAndPackageName to sharedPreferences.first())
                } else {
                    state
                }
            }
        }
    }

}
