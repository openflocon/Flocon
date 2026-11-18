package io.github.openflocon.domain.maintenance

import io.github.openflocon.domain.maintenance.usecase.AutoCleanMediaFilesUseCase
import io.github.openflocon.domain.maintenance.usecase.AutoPurgeDatabaseUseCase
import io.github.openflocon.domain.maintenance.usecase.CleanMediaFilesUseCase
import io.github.openflocon.domain.maintenance.usecase.GetDatabaseSizeUseCase
import io.github.openflocon.domain.maintenance.usecase.GetMediaFilesSizeUseCase
import io.github.openflocon.domain.maintenance.usecase.ObserveAutoMediaCleanupIntervalUseCase
import io.github.openflocon.domain.maintenance.usecase.ObserveAutoPurgeIntervalUseCase
import io.github.openflocon.domain.maintenance.usecase.PurgeDatabaseUseCase
import io.github.openflocon.domain.maintenance.usecase.SetAutoMediaCleanupIntervalUseCase
import io.github.openflocon.domain.maintenance.usecase.SetAutoPurgeIntervalUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val maintenanceModule = module {
    factoryOf(::GetDatabaseSizeUseCase)
    factoryOf(::PurgeDatabaseUseCase)
    factoryOf(::AutoPurgeDatabaseUseCase)
    factoryOf(::ObserveAutoPurgeIntervalUseCase)
    factoryOf(::SetAutoPurgeIntervalUseCase)
    factoryOf(::GetMediaFilesSizeUseCase)
    factoryOf(::CleanMediaFilesUseCase)
    factoryOf(::AutoCleanMediaFilesUseCase)
    factoryOf(::ObserveAutoMediaCleanupIntervalUseCase)
    factoryOf(::SetAutoMediaCleanupIntervalUseCase)
}
