package io.github.openflocon.flocondesktop

import com.florent37.flocondesktop.app.di.appModule
import com.florent37.flocondesktop.common.di.commonModule
import com.florent37.flocondesktop.core.di.coreModule
import com.florent37.flocondesktop.features.featuresModule
import com.florent37.flocondesktop.main.di.mainModule
import org.koin.core.context.startKoin

fun startKoinApp() {
    startKoin {
        modules(
            commonModule,
            appModule,
            coreModule,
            mainModule,
            featuresModule,
        )
    }
}
