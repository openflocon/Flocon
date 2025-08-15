package io.github.openflocon.data.core.sharedpreference.repository

import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesLocalDataSource
import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesRemoteDataSource
import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SharedPreferencesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val deviceSharedPreferencesDataSource: DeviceSharedPreferencesLocalDataSource,
    private val remote: DeviceSharedPreferencesRemoteDataSource,
    private val deviceSharedPreferencesValuesDataSource: DeviceSharedPreferencesValuesDataSource,
) : SharedPreferencesRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.SharedPreferences.Plugin)

    override suspend fun onMessageReceived(deviceId: String, message: FloconIncomingMessageDomainModel) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences -> {
                    val items = remote.getPreferences(message)

                    deviceSharedPreferencesDataSource.registerDeviceSharedPreferences(
                        deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                            deviceId = deviceId,
                            packageName = message.appPackageName,
                        ),
                        sharedPreferences = items,
                    )
                }

                Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue -> {
                    remote.getValues(message)
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
    }

    override suspend fun onNewDevice(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // no op
    }

    override fun observeSelectedDeviceSharedPreference(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceSharedPreferenceDomainModel?> = deviceSharedPreferencesDataSource
        .observeSelectedDeviceSharedPreference(deviceIdAndPackageName)
        .flowOn(dispatcherProvider.data)

    override fun selectDeviceSharedPreference(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) {
        deviceSharedPreferencesDataSource.selectDeviceSharedPreference(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = sharedPreferenceId,
        )
    }

    override fun observeDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceSharedPreferenceDomainModel>> = deviceSharedPreferencesDataSource
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

    override suspend fun askForDeviceSharedPreferences(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = withContext(dispatcherProvider.data) {
        remote.askForDeviceSharedPreferences(deviceIdAndPackageName = deviceIdAndPackageName)
    }

    override suspend fun getDeviceSharedPreferencesValues(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ) = withContext(dispatcherProvider.data) {
        remote.getDeviceSharedPreferencesValues(
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
            remote.editSharedPrefField(
                deviceIdAndPackageName = deviceIdAndPackageName,
                sharedPreference = sharedPreference,
                key = key,
                value = value,
            )
        }
    }
}
