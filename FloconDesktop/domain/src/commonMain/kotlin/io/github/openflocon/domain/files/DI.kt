package io.github.openflocon.domain.files

import io.github.openflocon.domain.files.usecase.DeleteFileUseCase
import io.github.openflocon.domain.files.usecase.DeleteFolderContentUseCase
import io.github.openflocon.domain.files.usecase.DownloadFileUseCase
import io.github.openflocon.domain.files.usecase.ObserveFolderContentUseCase
import io.github.openflocon.domain.files.usecase.RefreshFolderContentUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val filesModule = module {
    factoryOf(::ObserveFolderContentUseCase)
    factoryOf(::DeleteFolderContentUseCase)
    factoryOf(::DeleteFileUseCase)
    factoryOf(::RefreshFolderContentUseCase)
    factoryOf(::DownloadFileUseCase)
}
