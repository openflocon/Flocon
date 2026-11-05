package io.github.openflocon.data.core.timelinelog.repository

import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogLocalDataSource
import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.repository.TimelineLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TimelineLogRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val timelineLogLocalDataSource: TimelineLogLocalDataSource,
    private val timelineLogRemoteDataSource: TimelineLogRemoteDataSource,
) : TimelineLogRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Timeline.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Timeline.Method.Log -> {
                    timelineLogRemoteDataSource.getLog(message)?.let {
                        timelineLogLocalDataSource.insert(
                            deviceIdAndPackageName = deviceIdAndPackageName,
                            log = it,
                        )
                    }
                }
            }
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }

    override fun observeLog(id: Long): Flow<TimelineLogDomainModel?> = timelineLogLocalDataSource.observe(id)
        .flowOn(dispatcherProvider.data)
}
