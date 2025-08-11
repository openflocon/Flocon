package io.github.openflocon.flocondesktop.common.di

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProviderImpl
import io.github.openflocon.flocondesktop.common.db.roomModule
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
