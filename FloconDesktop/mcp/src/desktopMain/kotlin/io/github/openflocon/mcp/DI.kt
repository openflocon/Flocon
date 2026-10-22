package io.github.openflocon.mcp

import io.github.openflocon.domain.mcp.repository.McpServerRepository
import io.github.openflocon.mcp.analytics.AnalyticsMcpToolProvider
import io.github.openflocon.mcp.badquality.BadNetworkMcpToolProvider
import io.github.openflocon.mcp.capture.CaptureMcpToolProvider
import io.github.openflocon.mcp.capture.ScreenCapturer
import io.github.openflocon.mcp.capture.UseCaseScreenCapturer
import io.github.openflocon.mcp.crashreporter.CrashReporterMcpToolProvider
import io.github.openflocon.mcp.dashboard.DashboardMcpToolProvider
import io.github.openflocon.mcp.database.DatabaseMcpToolProvider
import io.github.openflocon.mcp.deeplink.DeeplinkMcpToolProvider
import io.github.openflocon.mcp.deeplink.DeeplinkOpener
import io.github.openflocon.mcp.deeplink.ExecuteDeeplinkOpener
import io.github.openflocon.mcp.devices.DevicesMcpToolProvider
import io.github.openflocon.mcp.documents.DesktopFolderOpener
import io.github.openflocon.mcp.documents.FloconFolderMcpToolProvider
import io.github.openflocon.mcp.documents.FolderOpener
import io.github.openflocon.mcp.featureflags.FeatureFlagsMcpToolProvider
import io.github.openflocon.mcp.files.FilesMcpToolProvider
import io.github.openflocon.mcp.images.ImagesMcpToolProvider
import io.github.openflocon.mcp.mocks.NetworkMocksMcpToolProvider
import io.github.openflocon.mcp.network.NetworkMcpToolProvider
import io.github.openflocon.mcp.sharedpreferences.SharedPreferencesMcpToolProvider
import io.github.openflocon.mcp.table.TableMcpToolProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mcpModule = module {
    single { FloconMcpServer(toolProviders = getAll(), logManager = get()) } bind McpServerRepository::class
    factoryOf(::McpTargetResolver)

    // tools, one provider per feature
    singleOf(::DevicesMcpToolProvider) bind McpToolProvider::class
    singleOf(::NetworkMcpToolProvider) bind McpToolProvider::class
    singleOf(::NetworkMocksMcpToolProvider) bind McpToolProvider::class
    singleOf(::BadNetworkMcpToolProvider) bind McpToolProvider::class
    singleOf(::FilesMcpToolProvider) bind McpToolProvider::class
    singleOf(::AnalyticsMcpToolProvider) bind McpToolProvider::class
    singleOf(::FeatureFlagsMcpToolProvider) bind McpToolProvider::class
    singleOf(::DatabaseMcpToolProvider) bind McpToolProvider::class
    singleOf(::ExecuteDeeplinkOpener) bind DeeplinkOpener::class
    singleOf(::DeeplinkMcpToolProvider) bind McpToolProvider::class
    singleOf(::UseCaseScreenCapturer) bind ScreenCapturer::class
    singleOf(::CaptureMcpToolProvider) bind McpToolProvider::class
    singleOf(::DesktopFolderOpener) bind FolderOpener::class
    singleOf(::FloconFolderMcpToolProvider) bind McpToolProvider::class
    singleOf(::SharedPreferencesMcpToolProvider) bind McpToolProvider::class
    singleOf(::DashboardMcpToolProvider) bind McpToolProvider::class
    singleOf(::ImagesMcpToolProvider) bind McpToolProvider::class
    singleOf(::CrashReporterMcpToolProvider) bind McpToolProvider::class
    singleOf(::TableMcpToolProvider) bind McpToolProvider::class
}
