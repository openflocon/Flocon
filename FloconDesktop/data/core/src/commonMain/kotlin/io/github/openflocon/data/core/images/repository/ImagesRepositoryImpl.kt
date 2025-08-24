package io.github.openflocon.data.core.images.repository

import io.github.openflocon.data.core.images.datasource.ImagesLocalDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import io.github.openflocon.domain.images.repository.ImagesRepository
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ImagesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val imagesLocalDataSource: ImagesLocalDataSource,
) : ImagesRepository,
    MessagesReceiverRepository,
    NetworkImageRepository {

    override val pluginName = listOf(Protocol.FromDevice.Images.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        // no op for now
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }

    override suspend fun onImageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        call: FloconNetworkCallDomainModel,
    ) {
        val duration = call.response?.durationMs ?: return
        imagesLocalDataSource.addImage(
            deviceIdAndPackageName = deviceIdAndPackageName,
            image = DeviceImageDomainModel(
                url = call.request.url,
                time = (call.request.startTime + duration).toLong(),
            ),
        )
    }

    override fun observeImages(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<List<DeviceImageDomainModel>> = imagesLocalDataSource
        .observeImages(deviceIdAndPackageName = deviceIdAndPackageName)
        .map { it.sortedBy { it.time } }
        .distinctUntilChanged()
        .flowOn(dispatcherProvider.data)

    override suspend fun clearImages(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            imagesLocalDataSource.clearImages(deviceIdAndPackageName = deviceIdAndPackageName)
        }
    }
}
