package io.github.openflocon.flocondesktop.features.deeplinks.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.executeAdbCommand
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.LocalDeeplinkDataSource
import io.github.openflocon.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import io.github.openflocon.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DeeplinkRepositoryImpl(
    private val localDeeplinkDataSource: LocalDeeplinkDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : DeeplinkRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Deeplink.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        when (message.method) {
            Protocol.FromDevice.Deeplink.Method.GetDeeplinks -> {
                decodeListDeeplinks(message.body)?.let {
                    localDeeplinkDataSource.update(
                        deviceId = deviceId,
                        it.toDomain(),
                    )
                }
            }
        }
    }

    override fun observe(deviceId: String): Flow<List<DeeplinkDomainModel>> = localDeeplinkDataSource.observe(deviceId)
        .flowOn(dispatcherProvider.data)

    override fun executeDeeplink(
        adbPath: String,
        deviceId: DeviceId,
        deeplink: String,
        packageName: String,
    ) {
        executeAdbCommand(
            adbPath = adbPath,
            // TODO inject the device serial
            command = "shell am start -W -a android.intent.action.VIEW -d \"$deeplink\" $packageName",
        )
    }
}
