package io.github.openflocon.flocondesktop.features.images.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.images.data.datasources.ImagesLocalDataSource
import com.florent37.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import com.florent37.flocondesktop.features.images.domain.repository.ImagesRepository
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.domain.repository.NetworkImageRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        // no op for now
    }

    override suspend fun onImageReceived(
        deviceId: String,
        request: FloconHttpRequestDomainModel,
    ) {
        imagesLocalDataSource.addImage(
            deviceId = deviceId,
            image = DeviceImageDomainModel(
                url = request.url,
                time = (request.request.startTime + request.durationMs).toLong(),
            ),
        )
    }

    override fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>> = imagesLocalDataSource
        .observeImages(deviceId = deviceId)
        .map { it.sortedBy { it.time } }
        .distinctUntilChanged()
        .flowOn(dispatcherProvider.data)

    override suspend fun clearImages(deviceId: DeviceId) {
        withContext(dispatcherProvider.data) {
            imagesLocalDataSource.clearImages(deviceId = deviceId)
        }
    }
}
