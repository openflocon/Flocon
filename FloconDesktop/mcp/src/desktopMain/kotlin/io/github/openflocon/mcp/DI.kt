package io.github.openflocon.mcp

import io.github.openflocon.domain.mcp.repository.McpServerRepository
import io.github.openflocon.mcp.devices.DevicesMcpToolProvider
import io.github.openflocon.mcp.network.NetworkMcpToolProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mcpModule = module {
    single { FloconMcpServer(toolProviders = getAll()) } bind McpServerRepository::class
    factoryOf(::McpTargetResolver)

    // tools, one provider per feature
    singleOf(::DevicesMcpToolProvider) bind McpToolProvider::class
    singleOf(::NetworkMcpToolProvider) bind McpToolProvider::class
}
