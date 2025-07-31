package io.github.openflocon.flocondesktop.features.sharedpreferences.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesDataSource
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.incoming.toDeviceSharedPreferenceDomain
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.incoming.toSharedPreferenceValuesResponseDomain
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
        deviceId: io.github.openflocon.flocondesktop.DeviceId,
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
