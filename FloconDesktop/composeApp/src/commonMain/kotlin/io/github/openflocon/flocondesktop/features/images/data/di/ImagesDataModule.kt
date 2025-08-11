package io.github.openflocon.flocondesktop.features.images.data.di

import io.github.openflocon.flocondesktop.features.images.data.ImagesRepositoryImpl
import io.github.openflocon.data.core.images.datasource.ImagesLocalDataSource
import io.github.openflocon.flocondesktop.features.images.data.datasources.ImagesLocalDataSourceRoom
import io.github.openflocon.domain.images.repository.ImagesRepository
import io.github.openflocon.domain.network.repository.NetworkImageRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val imagesDataModule =
    module {
        factoryOf(::ImagesRepositoryImpl) {
            bind<ImagesRepository>()
            bind<MessagesReceiverRepository>()
            bind<NetworkImageRepository>()
        }
        singleOf(::ImagesLocalDataSourceRoom) {
            bind<ImagesLocalDataSource>()
        }
    }
