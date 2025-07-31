package io.github.openflocon.flocondesktop.features.images.data.di

import com.florent37.flocondesktop.features.images.data.ImagesRepositoryImpl
import com.florent37.flocondesktop.features.images.data.datasources.ImagesLocalDataSource
import com.florent37.flocondesktop.features.images.data.datasources.ImagesLocalDataSourceRoom
import com.florent37.flocondesktop.features.images.domain.repository.ImagesRepository
import com.florent37.flocondesktop.features.network.domain.repository.NetworkImageRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
