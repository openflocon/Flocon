package io.github.openflocon.domain.maintenance

import io.github.openflocon.domain.maintenance.usecase.GetDatabaseSizeUseCase
import io.github.openflocon.domain.maintenance.usecase.PurgeDatabaseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val maintenanceModule = module {
    factoryOf(::GetDatabaseSizeUseCase)
    factoryOf(::PurgeDatabaseUseCase)
}
