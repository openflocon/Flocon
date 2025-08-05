package io.github.openflocon.flocondesktop.features.files.domain.di

import io.github.openflocon.flocondesktop.features.files.domain.DeleteFileUseCase
import io.github.openflocon.flocondesktop.features.files.domain.DeleteFolderContentUseCase
import io.github.openflocon.flocondesktop.features.files.domain.ObserveFolderContentUseCase
import io.github.openflocon.flocondesktop.features.files.domain.RefreshFolderContentUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val filesDomainModule =
    module {
        factoryOf(::ObserveFolderContentUseCase)
        factoryOf(::DeleteFolderContentUseCase)
        factoryOf(::DeleteFileUseCase)
        factoryOf(::RefreshFolderContentUseCase)
    }
