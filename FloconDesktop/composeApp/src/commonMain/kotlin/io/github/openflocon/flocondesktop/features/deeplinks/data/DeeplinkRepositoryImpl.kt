package io.github.openflocon.flocondesktop.features.deeplinks.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.data.core.deeplink.datasource.DeeplinkLocalDataSource
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DeeplinkRepositoryImpl(
    private val localDeeplinkDataSource: DeeplinkLocalDataSource,
    private val dispatcherProvider: DispatcherProvider,
    private val adbRepository: AdbRepository,
) : DeeplinkRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Deeplink.Plugin)

    override suspend fun onNewDevice(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        // no op
    }

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

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> = localDeeplinkDataSource.observe(deviceIdAndPackageName)
        .flowOn(dispatcherProvider.data)

    override fun executeDeeplink(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, adbPath: String, deeplink: String) {
        adbRepository.executeAdbCommand(
            adbPath = adbPath,
            // TODO inject the device serial
            command = "shell am start -W -a android.intent.action.VIEW -d \"$deeplink\" ${deviceIdAndPackageName.packageName}",
        )
    }
}
