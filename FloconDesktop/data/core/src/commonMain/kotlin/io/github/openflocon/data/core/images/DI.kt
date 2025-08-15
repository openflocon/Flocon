package io.github.openflocon.data.core.images

import io.github.openflocon.data.core.images.repository.ImagesRepositoryImpl
import io.github.openflocon.domain.images.repository.ImagesRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val imagesModule = module {
    singleOf(::ImagesRepositoryImpl) {
        bind<ImagesRepository>()
        bind<MessagesReceiverRepository>()
        bind<NetworkImageRepository>()
    }
}
