package io.github.openflocon.data.core.sharedpreference

import io.github.openflocon.data.core.sharedpreference.repository.SharedPreferencesRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val sharedPreferenceModule = module {
    singleOf(::SharedPreferencesRepositoryImpl) {
        bind<SharedPreferencesRepository>()
        bind<MessagesReceiverRepository>()
    }
}
