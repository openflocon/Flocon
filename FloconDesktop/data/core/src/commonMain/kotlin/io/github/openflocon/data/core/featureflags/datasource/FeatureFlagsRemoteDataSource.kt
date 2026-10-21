package io.github.openflocon.data.core.featureflags.datasource

import io.github.openflocon.domain.featureflags.models.FeatureFlagsProviderDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface FeatureFlagsRemoteDataSource {
    fun getFeatureFlags(message: FloconIncomingMessageDomainModel): FeatureFlagsProviderDomainModel?
}
