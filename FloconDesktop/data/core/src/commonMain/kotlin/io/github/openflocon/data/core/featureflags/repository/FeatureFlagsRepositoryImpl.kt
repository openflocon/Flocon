package io.github.openflocon.data.core.featureflags.repository

import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsLocalDataSource
import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FeatureFlagsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val featureFlagsLocalDataSource: FeatureFlagsLocalDataSource,
    private val featureFlagsRemoteDataSource: FeatureFlagsRemoteDataSource,
) : FeatureFlagsRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.FeatureFlags.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.FeatureFlags.Method.Register -> {
                    featureFlagsRemoteDataSource.getFeatureFlags(message)?.let {
                        featureFlagsLocalDataSource.replace(
                            deviceIdAndPackageName = deviceIdAndPackageName,
                            featureFlags = it,
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

    override fun observeFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: String?,
    ): Flow<List<FeatureFlagDomainModel>> = featureFlagsLocalDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        filter = filter,
    ).flowOn(dispatcherProvider.data)

    override fun observeFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): Flow<FeatureFlagDomainModel?> = featureFlagsLocalDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        provider = provider,
        name = name,
    ).flowOn(dispatcherProvider.data)

    override suspend fun searchFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String?,
        filter: String?,
        limit: Int,
    ): List<FeatureFlagDomainModel> = withContext(dispatcherProvider.data) {
        featureFlagsLocalDataSource.search(
            deviceIdAndPackageName = deviceIdAndPackageName,
            provider = provider,
            filter = filter,
            limit = limit,
        )
    }

    override suspend fun getFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): FeatureFlagDomainModel? = withContext(dispatcherProvider.data) {
        featureFlagsLocalDataSource.get(
            deviceIdAndPackageName = deviceIdAndPackageName,
            provider = provider,
            name = name,
        )
    }

    override suspend fun getProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String> = withContext(dispatcherProvider.data) {
        featureFlagsLocalDataSource.getProviders(deviceIdAndPackageName)
    }
}
