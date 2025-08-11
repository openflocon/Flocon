package io.github.openflocon.flocondesktop.features.sharedpreferences.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesDataSource
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.incoming.toDeviceSharedPreferenceDomain
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.incoming.toSharedPreferenceValuesResponseDomain
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
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
                    decodeDeviceSharedPreferences(message.body)
                        ?.let { toDeviceSharedPreferenceDomain(it) }
                        ?.let {
                            deviceSharedPreferencesDataSource.registerDeviceSharedPreferences(
                                deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName,
                                ),
                                sharedPreferences = it,
                            )
                        }

                Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue ->
                    decodeSharedPreferenceValuesResponse(
                        message.body,
                    )?.let { toSharedPreferenceValuesResponseDomain(it) }
                        ?.let {
                            deviceSharedPreferencesValuesDataSource.onSharedPreferencesValuesReceived(
                                deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName,
                                ),
                                received = it,
                            )
                        }
            }
        }
    }

    override fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?> =
        deviceSharedPreferencesDataSource
            .observeSelectedDeviceSharedPreference(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun selectDeviceSharedPreference(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId
    ) {
        deviceSharedPreferencesDataSource.selectDeviceSharedPreference(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = sharedPreferenceId,
        )
    }

    override fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceSharedPreferenceDomainModel>> =
        deviceSharedPreferencesDataSource
            .observeDeviceSharedPreferences(deviceIdAndPackageName = deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override suspend fun registerDeviceSharedPreferences(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferences: List<DeviceSharedPreferenceDomainModel>,
    ) = withContext(dispatcherProvider.data) {
        deviceSharedPreferencesDataSource.registerDeviceSharedPreferences(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferences = sharedPreferences,
        )
    }

    override suspend fun askForDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) =
        withContext(dispatcherProvider.data) {
            deviceSharedPreferencesDataSource.askForDeviceSharedPreferences(deviceIdAndPackageName = deviceIdAndPackageName)
        }

    override suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) = withContext(dispatcherProvider.data) {
        deviceSharedPreferencesDataSource.getDeviceSharedPreferencesValues(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = sharedPreferenceId,
        )
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>> = deviceSharedPreferencesValuesDataSource
        .observe(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = sharedPreferenceId,
        )
        .flowOn(dispatcherProvider.data)

    override suspend fun editSharedPrefField(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreference: DeviceSharedPreferenceDomainModel,
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    ) {
        withContext(dispatcherProvider.data) {
            deviceSharedPreferencesDataSource.editSharedPrefField(
                deviceIdAndPackageName = deviceIdAndPackageName,
                sharedPreference = sharedPreference,
                key = key,
                value = value,
            )
        }
    }
}
