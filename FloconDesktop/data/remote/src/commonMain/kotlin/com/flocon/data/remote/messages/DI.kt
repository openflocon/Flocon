package com.flocon.data.remote.messages

import com.flocon.data.remote.messages.datasource.MessageRemoteDataSourceImpl
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.server.getServer
import io.github.openflocon.data.core.messages.datasource.MessageRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val messagesModule = module {
    single<Server> { getServer() }
    singleOf(::MessageRemoteDataSourceImpl) bind MessageRemoteDataSource::class
}
