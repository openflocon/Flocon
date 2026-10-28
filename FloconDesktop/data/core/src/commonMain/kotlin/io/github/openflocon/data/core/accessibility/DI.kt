package io.github.openflocon.data.core.accessibility

import io.github.openflocon.data.core.accessibility.repository.AccessibilityRepositoryImpl
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val accessibilityModule = module {
    singleOf(::AccessibilityRepositoryImpl) {
        bind<AccessibilityRepository>()
        bind<MessagesReceiverRepository>()
    }
}
