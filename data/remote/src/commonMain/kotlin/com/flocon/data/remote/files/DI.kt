package com.flocon.data.remote.files

import com.flocon.data.remote.files.datasource.FilesRemoteDataSourceImpl
import io.github.openflocon.data.core.files.datasource.FilesRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val filesModule = module {
    singleOf(::FilesRemoteDataSourceImpl) bind FilesRemoteDataSource::class
}
