package com.flocon.data.remote.version

import com.flocon.data.remote.version.datasource.VersionCheckerDatasourceImpl
import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val versionModule = module {
    singleOf(::VersionCheckerDatasourceImpl) bind VersionCheckerDatasource::class
}
