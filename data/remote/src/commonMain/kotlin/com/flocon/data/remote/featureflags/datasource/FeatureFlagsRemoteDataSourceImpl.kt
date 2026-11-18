package com.flocon.data.remote.featureflags.datasource

import com.flocon.data.remote.featureflags.model.FeatureFlagsDataModel
import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsRemoteDataSource
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagStateDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagsProviderDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

class FeatureFlagsRemoteDataSourceImpl(
    private val json: Json,
) : FeatureFlagsRemoteDataSource {

    override fun getFeatureFlags(message: FloconIncomingMessageDomainModel): FeatureFlagsProviderDomainModel? = try {
        json.decodeFromString<FeatureFlagsDataModel>(message.body).toDomain()
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

private fun FeatureFlagsDataModel.toDomain() = FeatureFlagsProviderDomainModel(
    provider = provider,
    flags = flags.map {
        FeatureFlagDomainModel(
            provider = provider,
            name = it.name,
            state = stateToDomain(it.state),
            variant = it.variant,
            payload = it.payload,
        )
    },
)

private fun stateToDomain(state: String) = when (state.lowercase()) {
    "on" -> FeatureFlagStateDomainModel.On
    "off" -> FeatureFlagStateDomainModel.Off
    else -> FeatureFlagStateDomainModel.Custom
}
