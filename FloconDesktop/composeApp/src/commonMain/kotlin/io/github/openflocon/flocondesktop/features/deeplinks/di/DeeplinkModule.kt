package io.github.openflocon.flocondesktop.features.deeplinks.di

import com.florent37.flocondesktop.features.deeplinks.data.di.deeplinkDataModule
import com.florent37.flocondesktop.features.deeplinks.domain.di.deeplinkDomainModule
import com.florent37.flocondesktop.features.deeplinks.ui.di.deeplinkUiModule
import org.koin.dsl.module

val deeplinkModule =
    module {
        includes(
            deeplinkDataModule,
            deeplinkDomainModule,
            deeplinkUiModule,
        )
    }
