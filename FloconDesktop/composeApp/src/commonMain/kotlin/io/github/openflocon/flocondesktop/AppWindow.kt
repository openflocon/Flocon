@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.data.core.dataCoreModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.domain.maintenance.usecase.AutoCleanMediaFilesUseCase
import io.github.openflocon.domain.maintenance.usecase.AutoPurgeDatabaseUseCase
import io.github.openflocon.domain.models.settings.ThemeSetting
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

@Composable
fun App() {
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
        FloconTheme(
            appScale = appScale.toAppScale(),
            isDarkTheme = isDarkTheme,
        ) {
            AppScreen()
        }
//        }
    }
}
