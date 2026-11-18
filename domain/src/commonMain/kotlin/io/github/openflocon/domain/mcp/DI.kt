package io.github.openflocon.domain.mcp

import io.github.openflocon.domain.mcp.usecase.StartMcpServerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val mcpDomainModule = module {
    factoryOf(::StartMcpServerUseCase)
}
