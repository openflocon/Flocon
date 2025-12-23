package io.github.openflocon.domain.files

import io.github.openflocon.domain.files.usecase.DeleteFileUseCase
import io.github.openflocon.domain.files.usecase.DeleteFilesUseCase
import io.github.openflocon.domain.files.usecase.DeleteFolderContentUseCase
import io.github.openflocon.domain.files.usecase.DownloadFileUseCase
import io.github.openflocon.domain.files.usecase.ObserveFolderContentUseCase
import io.github.openflocon.domain.files.usecase.ObserveWithFoldersSizeUseCase
import io.github.openflocon.domain.files.usecase.RefreshFolderContentUseCase
import io.github.openflocon.domain.files.usecase.UpdateWithFoldersSizeUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val filesModule = module {
    factoryOf(::ObserveFolderContentUseCase)
    factoryOf(::DeleteFolderContentUseCase)
    factoryOf(::DeleteFileUseCase)
    factoryOf(::DeleteFilesUseCase)
    factoryOf(::RefreshFolderContentUseCase)
    factoryOf(::DownloadFileUseCase)
    factoryOf(::UpdateWithFoldersSizeUseCase)
    factoryOf(::ObserveWithFoldersSizeUseCase)
}
