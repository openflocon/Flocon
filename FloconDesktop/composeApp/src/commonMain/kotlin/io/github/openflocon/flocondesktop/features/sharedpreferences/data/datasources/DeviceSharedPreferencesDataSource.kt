package io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.server.newRequestId
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
import com.flocon.library.domain.models.DeviceSharedPreferenceDomainModel
import com.flocon.library.domain.models.DeviceSharedPreferenceId
import com.flocon.library.domain.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.todevice.ToDeviceEditSharedPreferenceValueMessage
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.todevice.ToDeviceGetSharedPreferenceValueMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class DeviceSharedPreferencesDataSource(
    private val server: Server,
) {
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

    suspend fun askForDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.SharedPreferences.Plugin,
                method = Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences,
                body = "",
            ),
        )
    }

    suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.SharedPreferences.Plugin,
                method = Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue,
                body = Json.encodeToString(
                    ToDeviceGetSharedPreferenceValueMessage(
                        requestId = requestId,
                        sharedPreferenceName = sharedPreferenceId,
                    ),
                ),
            ),
        )
    }

    suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    ) {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.SharedPreferences.Plugin,
                method = Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue,
                body = Json.encodeToString(
                    ToDeviceEditSharedPreferenceValueMessage(
                        requestId = requestId,
                        sharedPreferenceName = sharedPreference.id,
                        key = key,
                        stringValue = (value as? SharedPreferenceRowDomainModel.Value.StringValue)?.value,
                        intValue = (value as? SharedPreferenceRowDomainModel.Value.IntValue)?.value,
                        floatValue = (value as? SharedPreferenceRowDomainModel.Value.FloatValue)?.value,
                        booleanValue = (value as? SharedPreferenceRowDomainModel.Value.BooleanValue)?.value,
                        longValue = (value as? SharedPreferenceRowDomainModel.Value.LongValue)?.value,
                        setStringValue = (value as? SharedPreferenceRowDomainModel.Value.StringSetValue)?.value?.toList(),
                    ),
                ),
            ),
        )
    }
}
