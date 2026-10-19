package io.github.openflocon.mcp

import io.github.openflocon.domain.mcp.repository.McpServerRepository
import io.github.openflocon.mcp.analytics.AnalyticsMcpToolProvider
import io.github.openflocon.mcp.badquality.BadNetworkMcpToolProvider
import io.github.openflocon.mcp.database.DatabaseMcpToolProvider
import io.github.openflocon.mcp.devices.DevicesMcpToolProvider
import io.github.openflocon.mcp.files.FilesMcpToolProvider
import io.github.openflocon.mcp.mocks.NetworkMocksMcpToolProvider
import io.github.openflocon.mcp.network.NetworkMcpToolProvider
import io.github.openflocon.mcp.sharedpreferences.SharedPreferencesMcpToolProvider
import io.github.openflocon.mcp.table.TableMcpToolProvider
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
    singleOf(::NetworkMocksMcpToolProvider) bind McpToolProvider::class
    singleOf(::BadNetworkMcpToolProvider) bind McpToolProvider::class
    singleOf(::FilesMcpToolProvider) bind McpToolProvider::class
    singleOf(::AnalyticsMcpToolProvider) bind McpToolProvider::class
    singleOf(::DatabaseMcpToolProvider) bind McpToolProvider::class
    singleOf(::SharedPreferencesMcpToolProvider) bind McpToolProvider::class
    singleOf(::TableMcpToolProvider) bind McpToolProvider::class
}
