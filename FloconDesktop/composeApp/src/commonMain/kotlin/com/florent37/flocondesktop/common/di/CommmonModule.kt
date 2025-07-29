package com.florent37.flocondesktop.common.di

import com.florent37.flocondesktop.common.coroutines.closeable.CloseableDelegate
import com.florent37.flocondesktop.common.coroutines.closeable.CloseableScoped
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProviderImpl
import com.florent37.flocondesktop.common.db.roomModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule =
    module {
        includes(roomModule)
        singleOf(::DispatcherProviderImpl) {
            bind<DispatcherProvider>()
        }
        single {
            val dispatcherProvider = get<DispatcherProvider>()
            CoroutineScope(dispatcherProvider.data + SupervisorJob()) // the application scope
        }
        factoryOf(::CloseableDelegate) {
            bind<CloseableScoped>()
        }
    }
