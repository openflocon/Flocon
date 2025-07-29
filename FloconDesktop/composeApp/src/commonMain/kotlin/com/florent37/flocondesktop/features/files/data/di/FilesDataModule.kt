package com.florent37.flocondesktop.features.files.data.di

import com.florent37.flocondesktop.features.files.data.FilesRepositoryImpl
import com.florent37.flocondesktop.features.files.data.datasources.LocalFilesDataSource
import com.florent37.flocondesktop.features.files.data.datasources.LocalFilesDataSourceRoom
import com.florent37.flocondesktop.features.files.data.datasources.RemoteFilesDataSource
import com.florent37.flocondesktop.features.files.domain.repository.FilesRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
