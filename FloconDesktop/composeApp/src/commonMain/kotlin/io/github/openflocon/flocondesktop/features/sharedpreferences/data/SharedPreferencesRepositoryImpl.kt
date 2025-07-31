package io.github.openflocon.flocondesktop.features.sharedpreferences.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesDataSource
import com.florent37.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesValuesDataSource
import com.florent37.flocondesktop.features.sharedpreferences.data.model.incoming.toDeviceSharedPreferenceDomain
import com.florent37.flocondesktop.features.sharedpreferences.data.model.incoming.toSharedPreferenceValuesResponseDomain
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import com.florent37.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SharedPreferencesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val deviceSharedPreferencesDataSource: DeviceSharedPreferencesDataSource,
    private val deviceSharedPreferencesValuesDataSource: DeviceSharedPreferencesValuesDataSource,
) : SharedPreferencesRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.SharedPreferences.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences ->
                    decodeDeviceSharedPreferences(
                        message.body,
                    )?.let { toDeviceSharedPreferenceDomain(it) }
                        ?.let {
                            deviceSharedPreferencesDataSource.registerDeviceSharedPreferences(
                                deviceId = deviceId,
                                sharedPreferences = it,
                            )
                        }

                Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue ->
                    decodeSharedPreferenceValuesResponse(
                        message.body,
                    )?.let { toSharedPreferenceValuesResponseDomain(it) }
                        ?.let {
                            deviceSharedPreferencesValuesDataSource.onSharedPreferencesValuesReceived(
                                deviceId = deviceId,
                                received = it,
                            )
                        }
            }
        }
    }

    override fun observeSelectedDeviceSharedPreference(deviceId: DeviceId) = deviceSharedPreferencesDataSource
        .observeSelectedDeviceSharedPreference(deviceId)
        .flowOn(dispatcherProvider.data)

    override fun selectDeviceSharedPreference(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        deviceSharedPreferencesDataSource.selectDeviceSharedPreference(
            deviceId = deviceId,
            sharedPreferenceId = sharedPreferenceId,
        )
    }

    override fun observeDeviceSharedPreferences(deviceId: DeviceId) = deviceSharedPreferencesDataSource
        .observeDeviceSharedPreferences(deviceId)
        .flowOn(dispatcherProvider.data)

    override suspend fun registerDeviceSharedPreferences(
        deviceId: DeviceId,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    ) = withContext(dispatcherProvider.data) {
        deviceSharedPreferencesDataSource.registerDeviceSharedPreferences(
            deviceId = deviceId,
            sharedPreferences = sharedPreferences,
        )
    }

    override suspend fun askForDeviceSharedPreferences(deviceId: DeviceId) = withContext(dispatcherProvider.data) {
        deviceSharedPreferencesDataSource.askForDeviceSharedPreferences(deviceId = deviceId)
    }

    override suspend fun getDeviceSharedPreferencesValues(
        deviceId: DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) = withContext(dispatcherProvider.data) {
        deviceSharedPreferencesDataSource.getDeviceSharedPreferencesValues(
            deviceId = deviceId,
            sharedPreferenceId = sharedPreferenceId,
        )
    }

    override fun observe(
        deviceId: com.florent37.flocondesktop.DeviceId,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>> = deviceSharedPreferencesValuesDataSource
        .observe(
            deviceId = deviceId,
            sharedPreferenceId = sharedPreferenceId,
        ).flowOn(dispatcherProvider.data)

    override suspend fun editSharedPrefField(
        deviceId: DeviceId,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    ) {
        withContext(dispatcherProvider.data) {
            deviceSharedPreferencesDataSource.editSharedPrefField(
                deviceId = deviceId,
                sharedPreference = sharedPreference,
                key = key,
                value = value,
            )
        }
    }
}
