package io.github.openflocon.data.core.deeplink.repository

import io.github.openflocon.data.core.deeplink.datasource.DeeplinkLocalDataSource
import io.github.openflocon.data.core.deeplink.datasource.DeeplinkRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DeeplinkRepositoryImpl(
    private val localDeeplinkDataSource: DeeplinkLocalDataSource,
    private val remote: DeeplinkRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : DeeplinkRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Deeplink.Plugin)

    override suspend fun onMessageReceived(deviceId: String, message: FloconIncomingMessageDomainModel) {
        when (message.method) {
            Protocol.FromDevice.Deeplink.Method.GetDeeplinks -> {
                val items = remote.getItems(message)

                localDeeplinkDataSource.update(
                    deviceIdAndPackageNameDomainModel = DeviceIdAndPackageNameDomainModel(
                        deviceId = deviceId,
                        packageName = message.appPackageName,
                    ),
                    deeplinks = items
                )
            }
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> = localDeeplinkDataSource.observe(deviceIdAndPackageName)
        .flowOn(dispatcherProvider.data)
}
