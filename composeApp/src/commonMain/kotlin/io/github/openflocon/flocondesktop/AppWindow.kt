@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.flocon.data.remote.dataRemoteModule
import com.flocon.data.remote.mock.MockScenario
import io.github.openflocon.data.core.dataCoreModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.domain.maintenance.usecase.AutoCleanMediaFilesUseCase
import io.github.openflocon.domain.maintenance.usecase.AutoPurgeDatabaseUseCase
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.proxy.usecase.RunProxyUseCase
import io.github.openflocon.domain.settings.usecase.ObserveAppLanguageUseCase
import io.github.openflocon.domain.settings.usecase.ObserveAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.flocondesktop.adb.AdbRepositoryImpl
import io.github.openflocon.flocondesktop.app.AppScreen
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.app.ui.settings.toAppScale
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.features.timeline.TimelineRoutes
import io.github.openflocon.flocondesktop.iossimulator.IosSimulatorRepositoryImpl
import io.github.openflocon.flocondesktop.mock.mockModule
import io.github.openflocon.flocondesktop.telemetry.telemetryModule
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.mcp.mcpModule
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.Locale

// the locale of the computer, to come back to it when the language is "System"
private val systemLocale: Locale = Locale.getDefault()

@Composable
fun App(
    serverPortsOverrides: ServerPortsOverrides = ServerPortsOverrides.None,
    mockScenario: MockScenario? = null,
) {
    ComposeFoundationFlags.isNewContextMenuEnabled = true

    KoinApplication(
        application = {
            modules(
                commonModule,
                appModule,
                coreModule,
                featuresModule,
                domainModule,
                dataCoreModule,
                dataLocalModule,
                dataRemoteModule,
                mcpModule,
                telemetryModule,
                // the ports given as launch options, for this session only
                module { single { serverPortsOverrides } },
                // Temporary
                module {
//                    scope<MainRoutes.Sub> {
//                        scoped { MainFloconNavigationState(MainRoutes.Main) }
//                    }
                    single { MainFloconNavigationState(TimelineRoutes.Main) }
                    singleOf(::AdbRepositoryImpl) bind AdbRepository::class
                    singleOf(::IosSimulatorRepositoryImpl) bind IosSimulatorRepository::class
                },
            )
            // last, to override the real devices with the scenario of `--mock=<name>`
            mockScenario?.let { modules(mockModule(it)) }
        },
    ) {
        val autoPurgeDatabase = koinInject<AutoPurgeDatabaseUseCase>()
        val autoCleanMediaFiles = koinInject<AutoCleanMediaFilesUseCase>()
        LaunchedEffect(Unit) {
            // the old data is deleted at each launch, if the user asked for it in the settings
            withContext(Dispatchers.IO) {
                runCatching { autoPurgeDatabase() }
                    .onFailure { Logger.e("App", it) { "Automatic database purge failed" } }
                runCatching { autoCleanMediaFiles() }
                    .onFailure { Logger.e("App", it) { "Automatic screenshots and recordings cleanup failed" } }
            }
        }

        val runProxy = koinInject<RunProxyUseCase>()
        LaunchedEffect(Unit) {
            // started at launch when the user left it on, see the proxy mode dialog
            withContext(Dispatchers.IO) { runProxy() }
        }

        val appScale by koinInject<ObserveAppScaleUseCase>()()
            .collectAsStateWithLifecycle()
        val theme by koinInject<ObserveThemeUseCase>()()
            .collectAsStateWithLifecycle()
        val isDarkTheme = when (theme) {
            ThemeSetting.Dark -> true
            ThemeSetting.Light -> false
            ThemeSetting.System -> isSystemInDarkTheme()
        }

//        KoinScope(
//            scopeDefinition = { createScope(MainRoutes.Sub.Main) }
//        ) {
        val appLanguage by koinInject<ObserveAppLanguageUseCase>()()
            .collectAsStateWithLifecycle()
        remember(appLanguage) {
            // the resources read the default locale to know the language to load
            Locale.setDefault(appLanguage.languageTag?.let(Locale::forLanguageTag) ?: systemLocale)
        }

        // the resources are loaded again when the density changes: a tiny change makes the strings follow the new language,
        // without composing the screens from scratch (which would close the dialogs)
        val density = LocalDensity.current
        val languageDensity = remember(density, appLanguage) {
            Density(density.density, density.fontScale * (1f + 1e-5f * appLanguage.ordinal))
        }
        CompositionLocalProvider(LocalDensity provides languageDensity) {
            FloconTheme(
                appScale = appScale.toAppScale(),
                isDarkTheme = isDarkTheme,
            ) {
                AppScreen()
            }
        }
//        }
    }
}
