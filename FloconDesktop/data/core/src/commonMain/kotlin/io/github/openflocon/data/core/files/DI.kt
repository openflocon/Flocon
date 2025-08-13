package io.github.openflocon.data.core.files

import io.github.openflocon.data.core.files.repository.FilesRepositoryImpl
import io.github.openflocon.domain.files.repository.FilesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val filesModule = module {
    singleOf(::FilesRepositoryImpl) bind FilesRepository::class
}
