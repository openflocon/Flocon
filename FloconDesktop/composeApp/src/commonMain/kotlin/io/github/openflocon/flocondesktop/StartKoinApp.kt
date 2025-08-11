package io.github.openflocon.flocondesktop

import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.flocondesktop.adb.AdbRepositoryImpl
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.main.di.mainModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun startKoinApp() {
    startKoin {
        modules(
            commonModule,
            appModule,
            coreModule,
            mainModule,
            featuresModule,
            domainModule,
            dataRemoteModule,
            // Temporary
            module {
                singleOf(::AdbRepositoryImpl) bind AdbRepository::class
            }
        )
    }
}
