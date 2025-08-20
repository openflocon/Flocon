package com.flocon.data.remote.sharedpreference.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.server.newRequestId
import com.flocon.data.remote.sharedpreference.mapper.SharedPreferenceValuesResponse
import com.flocon.data.remote.sharedpreference.mapper.toSharedPreferenceValuesResponseDomain
import com.flocon.data.remote.sharedpreference.models.DeviceSharedPreferenceDataModel
import com.flocon.data.remote.sharedpreference.models.ToDeviceEditSharedPreferenceValueMessage
import com.flocon.data.remote.sharedpreference.models.ToDeviceGetSharedPreferenceValueMessage
import com.flocon.data.remote.sharedpreference.models.toDeviceSharedPreferenceDomain
import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceValuesResponseDomainModel
import kotlinx.serialization.json.Json

class DeviceSharedPreferencesRemoteDataSourceImpl(
    private val server: Server,
    private val json: Json,
) : DeviceSharedPreferencesRemoteDataSource {

    override suspend fun askForDeviceSharedPreferences(
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

    override suspend fun getDeviceSharedPreferencesValues(
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

    override suspend fun editSharedPrefField(
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

    override fun getPreferences(message: FloconIncomingMessageDomainModel): List<DeviceSharedPreferenceDomainModel> = json.safeDecodeFromString<List<DeviceSharedPreferenceDataModel>>(message.body)
        ?.let { toDeviceSharedPreferenceDomain(it) }
        .orEmpty()

    override fun getValues(message: FloconIncomingMessageDomainModel): SharedPreferenceValuesResponseDomainModel? = json.safeDecodeFromString<SharedPreferenceValuesResponse>(message.body)
        ?.let { toSharedPreferenceValuesResponseDomain(it) }
}
