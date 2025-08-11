package com.flocon.data.remote.table

import com.flocon.data.remote.table.datasource.TableRemoteDataSourceImpl
import io.github.openflocon.data.core.table.datasource.TableRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val tableModule = module {
    singleOf(::TableRemoteDataSourceImpl) bind TableRemoteDataSource::class
}
