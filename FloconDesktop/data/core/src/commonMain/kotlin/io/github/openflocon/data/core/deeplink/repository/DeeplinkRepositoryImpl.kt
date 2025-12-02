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
import kotlinx.coroutines.withContext

class DeeplinkRepositoryImpl(
    private val localDeeplinkDataSource: DeeplinkLocalDataSource,
    private val remote: DeeplinkRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : DeeplinkRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Deeplink.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        when (message.method) {
            Protocol.FromDevice.Deeplink.Method.GetDeeplinks -> {
                val items = remote.getItems(message)

                println(items.toString())

                localDeeplinkDataSource.update(
                    deviceIdAndPackageNameDomainModel = deviceIdAndPackageName,
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

    override suspend fun getDeeplinkById(
        deeplinkId: Long,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): DeeplinkDomainModel? = withContext(dispatcherProvider.data) {
        localDeeplinkDataSource.getDeeplinkById(deeplinkId, deviceIdAndPackageName)
    }

    override fun observeHistory(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> = localDeeplinkDataSource.observeHistory(deviceIdAndPackageName)
        .flowOn(dispatcherProvider.data)

    override suspend fun addToHistory(
        item: DeeplinkDomainModel,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            localDeeplinkDataSource.addToHistory(
                deviceIdAndPackageName = deviceIdAndPackageName,
                item = item,
            )
        }
    }

    override suspend fun removeFromHistory(
        deeplinkId: Long,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            localDeeplinkDataSource.removeFromHistory(
                deviceIdAndPackageName = deviceIdAndPackageName,
                deeplinkId = deeplinkId
            )
        }
    }
}
