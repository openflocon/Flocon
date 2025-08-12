package com.flocon.data.remote.messages

import com.flocon.data.remote.messages.datasource.MessageRemoteDataSourceImpl
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.server.getServer
import io.github.openflocon.data.core.messages.datasource.MessageRemoteDataSource
import io.github.openflocon.data.core.messages.repository.MessagesRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val messagesModule = module {
    single<Server> { getServer() }
    singleOf(::MessagesRepositoryImpl) bind MessagesRepository::class
    singleOf(::MessageRemoteDataSourceImpl) bind MessageRemoteDataSource::class
}
