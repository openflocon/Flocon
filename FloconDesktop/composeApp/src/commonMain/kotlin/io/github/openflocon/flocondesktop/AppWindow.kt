@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.data.core.dataCoreModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.flocondesktop.adb.AdbRepositoryImpl
import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayerView
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.analytics.analyticsRoutes
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.features.network.networkRoutes
import io.github.openflocon.flocondesktop.menu.MainRoutes
import io.github.openflocon.flocondesktop.menu.di.mainModule
import io.github.openflocon.flocondesktop.menu.menuRoutes
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.navigation.FloconNavigation
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
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
                mainModule,
                featuresModule,
                domainModule,
                dataCoreModule,
                dataLocalModule,
                dataRemoteModule,
                // Temporary
                module {
//                    scope<MainRoutes.Sub> {
//                        scoped { MainFloconNavigationState(MainRoutes.Main) }
//                    }
                    single { MainFloconNavigationState(MainRoutes.Main) }
                    singleOf(::AdbRepositoryImpl) bind AdbRepository::class
                },
            )
        },
    ) {
        val fontSizeMultiplier by koinInject<ObserveFontSizeMultiplierUseCase>()()
            .collectAsStateWithLifecycle()

//        KoinScope(
//            scopeDefinition = { createScope(MainRoutes.Sub.Main) }
//        ) {
        FloconTheme(
            fontSizeMultiplier = fontSizeMultiplier
        ) {
            val viewModel: AppViewModel = koinViewModel()

            Content(
                navigationState = viewModel.navigationState,
            )
        }
//        }
    }
}

@Composable
private fun Content(
    navigationState: MainFloconNavigationState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloconNavigation(
            navigationState = navigationState,
            sceneStrategy = PanelSceneStrategy()
                .then(WindowSceneStrategy())
                .then(DialogSceneStrategy())
                .then(SinglePaneSceneStrategy()),
            modifier = Modifier
                .matchParentSize()
                .background(FloconTheme.colorPalette.surface)
        ) {
            menuRoutes()
            networkRoutes()
            analyticsRoutes()
        }
        FeedbackDisplayerView()
    }
}
