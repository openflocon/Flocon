package io.github.openflocon.data.core.accessibility.repository

import io.github.openflocon.data.core.accessibility.datasource.AccessibilityLocalDataSource
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityRemoteDataSource
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityStateDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AccessibilityRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val accessibilityLocalDataSource: AccessibilityLocalDataSource,
    private val accessibilityRemoteDataSource: AccessibilityRemoteDataSource,
    private val accessibilityStateDataSource: AccessibilityStateDataSource,
) : AccessibilityRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Accessibility.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Accessibility.Method.AddEvents -> {
                    val events = accessibilityRemoteDataSource.getEvents(message)
                    if (events.isNotEmpty()) {
                        accessibilityLocalDataSource.insert(
                            deviceIdAndPackageName = deviceIdAndPackageName,
                            events = events,
                        )
                    }
                }

                Protocol.FromDevice.Accessibility.Method.State -> {
                    accessibilityRemoteDataSource.getState(message)?.let {
                        accessibilityStateDataSource.set(
                            deviceIdAndPackageName = deviceIdAndPackageName,
                            state = it,
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

    override fun observeEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
    ): Flow<List<AccessibilityEventDomainModel>> = accessibilityLocalDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        type = type,
        text = text,
    ).flowOn(dispatcherProvider.data)

    override fun observeEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ): Flow<AccessibilityEventDomainModel?> = accessibilityLocalDataSource.observeById(
        deviceIdAndPackageName = deviceIdAndPackageName,
        id = id,
    ).flowOn(dispatcherProvider.data)

    override fun observeTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>> = accessibilityLocalDataSource.observeTypes(deviceIdAndPackageName)
        .flowOn(dispatcherProvider.data)

    override fun observeState(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<AccessibilityStateDomainModel?> = accessibilityStateDataSource.observe(deviceIdAndPackageName)

    override suspend fun searchEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
        limit: Int,
    ): List<AccessibilityEventDomainModel> = withContext(dispatcherProvider.data) {
        accessibilityLocalDataSource.search(
            deviceIdAndPackageName = deviceIdAndPackageName,
            type = type,
            text = text,
            limit = limit,
        )
    }

    override suspend fun getTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String> = withContext(dispatcherProvider.data) {
        accessibilityLocalDataSource.getTypes(deviceIdAndPackageName)
    }

    override suspend fun getState(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): AccessibilityStateDomainModel? = accessibilityStateDataSource.get(deviceIdAndPackageName)

    override suspend fun clearEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Int = withContext(dispatcherProvider.data) {
        accessibilityLocalDataSource.clear(deviceIdAndPackageName)
    }
}
