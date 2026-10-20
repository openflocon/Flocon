package com.flocon.data.remote.featureflags.datasource

import com.flocon.data.remote.featureflags.model.FeatureFlagsDataModel
import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsRemoteDataSource
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
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
            variant = it.variant,
            payload = it.payload,
        )
    },
)
