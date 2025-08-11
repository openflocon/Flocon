package io.github.openflocon.flocondesktop

import io.github.openflocon.domain.domainModule
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.main.di.mainModule
import org.koin.core.context.startKoin

fun startKoinApp() {
    startKoin {
        modules(
            commonModule,
            appModule,
            coreModule,
            mainModule,
            featuresModule,
            domainModule
        )
    }
}
