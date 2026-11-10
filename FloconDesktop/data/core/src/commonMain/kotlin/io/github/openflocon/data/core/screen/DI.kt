package io.github.openflocon.data.core.screen

import io.github.openflocon.data.core.screen.repository.DesktopNavigationRepositoryImpl
import io.github.openflocon.domain.screen.repository.DesktopNavigationRepository
import org.koin.dsl.module

internal val screenModule = module {
    single<DesktopNavigationRepository> { DesktopNavigationRepositoryImpl() }
}
