package io.github.openflocon.flocondesktop.features.files.data.di

import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.flocondesktop.features.files.data.FilesRepositoryImpl
import io.github.openflocon.data.core.files.datasource.FilesRemoteDataSource
import com.flocon.data.remote.files.datasource.FilesRemoteDataSourceImpl
import io.github.openflocon.data.core.files.datasource.FilesLocalDataSource
import io.github.openflocon.data.local.files.datasource.LocalFilesDataSourceRoom
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val filesDataModule =
    module {
        factoryOf(::FilesRepositoryImpl) {
            bind<FilesRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::LocalFilesDataSourceRoom) {
            bind<FilesLocalDataSource>()
        }
        singleOf(::FilesRemoteDataSourceImpl) bind FilesRemoteDataSource::class
    }
