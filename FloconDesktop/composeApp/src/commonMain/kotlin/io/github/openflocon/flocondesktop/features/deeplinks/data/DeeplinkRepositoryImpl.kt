package io.github.openflocon.flocondesktop.features.deeplinks.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.executeAdbCommand
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.LocalDeeplinkDataSource
import io.github.openflocon.domain.models.DeeplinkDomainModel
import io.github.openflocon.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
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
                        deviceIdAndPackageNameDomainModel = DeviceIdAndPackageNameDomainModel(
                            deviceId = deviceId,
                            packageName = message.appPackageName,
                        ),
                        deeplinks = it.toDomain(),
                    )
                }
            }
        }
    }

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> =
        localDeeplinkDataSource.observe(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun executeDeeplink(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, adbPath: String, deeplink: String) {
        executeAdbCommand(
            adbPath = adbPath,
            // TODO inject the device serial
            command = "shell am start -W -a android.intent.action.VIEW -d \"$deeplink\" ${deviceIdAndPackageName.packageName}",
        )
    }
}
