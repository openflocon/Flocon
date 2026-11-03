@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.data.core.dataCoreModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
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
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Starts Koin for the whole application: it has to be above the windows, which all share it.
 * The windows opened for a device are scopes of it, see [io.github.openflocon.flocondesktop.window.DeviceWindowScope].
 */
@Composable
fun FloconKoinApplication(content: @Composable () -> Unit) {
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
                    single { MainFloconNavigationState(TimelineRoutes.Main) }
                    singleOf(::AdbRepositoryImpl) bind AdbRepository::class
                    singleOf(::IosSimulatorRepositoryImpl) bind IosSimulatorRepository::class
                },
            )
        },
        content = content,
    )
}

/** The theme chosen in the settings. Read without a lifecycle: it can sit above the windows, which provide it. */
@Composable
fun FloconAppTheme(content: @Composable () -> Unit) {
    val appScale by koinInject<ObserveAppScaleUseCase>()().collectAsState()
    val theme by koinInject<ObserveThemeUseCase>()().collectAsState()
    val isDarkTheme = when (theme) {
        ThemeSetting.Dark -> true
        ThemeSetting.Light -> false
        ThemeSetting.System -> isSystemInDarkTheme()
    }

    FloconTheme(
        appScale = appScale.toAppScale(),
        isDarkTheme = isDarkTheme,
        content = content,
    )
}

@Composable
fun App() {
    FloconAppTheme {
        AppScreen()
    }
}
