package io.github.openflocon.flocondesktop.features.files.domain.di

import com.florent37.flocondesktop.features.files.domain.DeleteFileUseCase
import com.florent37.flocondesktop.features.files.domain.DeleteFolderContentUseCase
import com.florent37.flocondesktop.features.files.domain.ObserveFolderContentUseCase
import com.florent37.flocondesktop.features.files.domain.RefreshFolderContentUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val filesDomainModule =
    module {
        factoryOf(::ObserveFolderContentUseCase)
        factoryOf(::DeleteFolderContentUseCase)
        factoryOf(::DeleteFileUseCase)
        factoryOf(::RefreshFolderContentUseCase)
    }
