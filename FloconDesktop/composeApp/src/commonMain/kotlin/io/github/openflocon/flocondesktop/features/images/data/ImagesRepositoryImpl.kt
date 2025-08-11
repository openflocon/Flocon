package io.github.openflocon.flocondesktop.features.images.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.DeviceId
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.images.data.datasources.ImagesLocalDataSource
import io.github.openflocon.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import io.github.openflocon.flocondesktop.features.images.domain.repository.ImagesRepository
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkImageRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
