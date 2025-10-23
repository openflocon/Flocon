package io.github.openflocon.data.core.versions

import io.github.openflocon.data.core.versions.repository.VersionCheckerRepositoryImpl
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val versionModule = module {
    singleOf(::VersionCheckerRepositoryImpl) {
        bind<VersionsCheckerRepository>()
    }
}
