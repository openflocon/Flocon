package io.github.openflocon.flocondesktop.features.network.di

import com.florent37.flocondesktop.features.network.data.di.networkDataModule
import com.florent37.flocondesktop.features.network.domain.di.networkDomainModule
import com.florent37.flocondesktop.features.network.ui.di.networkUiModule
import org.koin.dsl.module

val networkModule =
    module {
        includes(
            networkDataModule,
            networkDomainModule,
            networkUiModule,
        )
    }
