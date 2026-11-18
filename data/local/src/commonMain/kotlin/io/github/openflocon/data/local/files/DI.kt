package io.github.openflocon.data.local.files

import io.github.openflocon.data.core.files.datasource.FilesLocalDataSource
import io.github.openflocon.data.local.files.datasource.LocalFilesDataSourceRoom
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val filesModule = module {
    singleOf(::LocalFilesDataSourceRoom) bind FilesLocalDataSource::class
}
