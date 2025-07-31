package io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconOutgoingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.Server
import com.florent37.flocondesktop.features.sharedpreferences.data.model.todevice.ToDeviceEditSharedPreferenceValueMessage
import com.florent37.flocondesktop.features.sharedpreferences.data.model.todevice.ToDeviceGetSharedPreferenceValueMessage
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import com.florent37.flocondesktop.newRequestId
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
        MutableStateFlow<Map<DeviceId, List<DeviceSharedPreferenceDomainModel>>>(emptyMap())

    private val selectedDeviceSharedPreferences =
        MutableStateFlow<Map<DeviceId, DeviceSharedPreferenceDomainModel?>>(emptyMap())

    fun observeSelectedDeviceSharedPreference(deviceId: DeviceId): Flow<DeviceSharedPreferenceDomainModel?> = selectedDeviceSharedPreferences
        .map {
            it[deviceId]
        }.distinctUntilChanged()

    fun selectDeviceSharedPreference(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        val deviceSharedPreferenceList = deviceSharedPreferences.value[deviceId] ?: return
        val sharedPreference =
            deviceSharedPreferenceList.firstOrNull { it.id == sharedPreferenceId } ?: return

        selectedDeviceSharedPreferences.update {
            it + (deviceId to sharedPreference)
        }
    }

    fun observeDeviceSharedPreferences(deviceId: DeviceId): Flow<List<DeviceSharedPreferenceDomainModel>> = deviceSharedPreferences.map {
        it[deviceId] ?: emptyList()
    }

    fun registerDeviceSharedPreferences(
        deviceId: DeviceId,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    ) {
        deviceSharedPreferences.update {
            val actual = it[deviceId]
            val newList =
                buildList<DeviceSharedPreferenceDomainModel> {
                    actual?.let { addAll(it) }
                    addAll(sharedPreferences)
                }.distinct()
            it + (deviceId to newList)
        }

        if (sharedPreferences.isNotEmpty()) {
            // select the first db if no one for this device id
            selectedDeviceSharedPreferences.update { state ->
                val dbForThisDevice = state[deviceId]
                if (dbForThisDevice == null) {
                    state + (deviceId to sharedPreferences.first())
                } else {
                    state
                }
            }
        }
    }

    suspend fun askForDeviceSharedPreferences(deviceId: DeviceId) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.SharedPreferences.Plugin,
                method = Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences,
                body = "",
            ),
        )
    }

    suspend fun getDeviceSharedPreferencesValues(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.SharedPreferences.Plugin,
                method = Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue,
                body =
                Json.encodeToString(
                    ToDeviceGetSharedPreferenceValueMessage(
                        requestId = requestId,
                        sharedPreferenceName = sharedPreferenceId,
                    ),
                ),
            ),
        )
    }

    suspend fun editSharedPrefField(
        deviceId: DeviceId,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    ) {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
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
