package io.github.openflocon.domain.connect

import io.github.openflocon.domain.connect.usecase.GetLocalAddressesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val connectModule = module {
    factoryOf(::GetLocalAddressesUseCase)
}
