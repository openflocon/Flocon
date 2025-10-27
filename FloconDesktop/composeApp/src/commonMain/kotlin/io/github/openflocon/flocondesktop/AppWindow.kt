@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
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
import io.github.openflocon.flocondesktop.app.AppAction
import io.github.openflocon.flocondesktop.app.AppUiState
import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.analytics.analyticsRoutes
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.network.networkRoutes
import io.github.openflocon.flocondesktop.menu.MenuSceneStrategy
import io.github.openflocon.flocondesktop.menu.di.mainModule
import io.github.openflocon.flocondesktop.menu.ui.buildLeftPanelState
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.menu.ui.view.leftpannel.LeftPanelView
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
                    single { MainFloconNavigationState(NetworkRoutes.Main) }
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
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Content(
                uiState = uiState,
                navigationState = viewModel.navigationState,
                onAction = viewModel::onAction
            )
        }
//        }
    }
}

@Composable
private fun Content(
    uiState: AppUiState,
    navigationState: MainFloconNavigationState,
    onAction: (AppAction) -> Unit
) {
    // TODO Redo
    val menuState by produceState(
        buildLeftPanelState(SubScreen.Network),
        uiState.contentState.current
    ) {
        value = buildLeftPanelState(uiState.contentState.current)
    }

    FloconNavigation(
        navigationState = navigationState,
        sceneStrategy = PanelSceneStrategy()
            .then(WindowSceneStrategy())
            .then(DialogSceneStrategy())
            .then(MenuSceneStrategy {
                LeftPanelView(
                    state = menuState,
                    expanded = it,
                    onClickItem = { menu -> onAction(AppAction.SelectMenu(menu.screen)) }
                )
            })
            .then(SinglePaneSceneStrategy()),
        modifier = Modifier
            .fillMaxSize()
            .background(FloconTheme.colorPalette.surface)
    ) {
        networkRoutes()
        analyticsRoutes()
    }
}
