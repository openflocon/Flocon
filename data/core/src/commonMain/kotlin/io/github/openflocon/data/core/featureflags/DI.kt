package io.github.openflocon.data.core.featureflags

import io.github.openflocon.data.core.featureflags.repository.FeatureFlagsRepositoryImpl
import io.github.openflocon.domain.featureflags.repository.FeatureFlagsRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val featureFlagsModule = module {
    singleOf(::FeatureFlagsRepositoryImpl) {
        bind<FeatureFlagsRepository>()
        bind<MessagesReceiverRepository>()
    }
}
