package io.github.openflocon.flocondesktop.features.files.data.di

import io.github.openflocon.flocondesktop.features.files.data.FilesRepositoryImpl
import io.github.openflocon.flocondesktop.features.files.data.datasources.LocalFilesDataSource
import io.github.openflocon.flocondesktop.features.files.data.datasources.LocalFilesDataSourceRoom
import io.github.openflocon.flocondesktop.features.files.data.datasources.RemoteFilesDataSource
import io.github.openflocon.flocondesktop.features.files.domain.repository.FilesRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val filesDataModule =
    module {
        factoryOf(::FilesRepositoryImpl) {
            bind<FilesRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::LocalFilesDataSourceRoom) {
            bind<LocalFilesDataSource>()
        }
        singleOf(::RemoteFilesDataSource)
    }
