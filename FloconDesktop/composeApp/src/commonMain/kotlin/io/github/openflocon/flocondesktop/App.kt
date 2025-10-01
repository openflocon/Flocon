@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.data.core.dataCoreModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.flocondesktop.adb.AdbRepositoryImpl
import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.app.version.VersionCheckerView
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.features.network.NetworkRoute
import io.github.openflocon.flocondesktop.features.network.networkNavigation
import io.github.openflocon.flocondesktop.main.di.mainModule
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.LeftPanelView
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.panel.FloconPanelDisplayer
import io.github.openflocon.library.designsystem.components.panel.LocalFloconPanelController
import io.github.openflocon.library.designsystem.components.panel.rememberFloconPanelController
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
                navigationModule,
                // Temporary
                module {
                    singleOf(::AdbRepositoryImpl) bind AdbRepository::class
                },
            )
        },
    ) {
        val fontSizeMultiplier by koinInject<ObserveFontSizeMultiplierUseCase>()()
            .collectAsStateWithLifecycle()

        val panelController = rememberFloconPanelController()

        FloconTheme(
            fontSizeMultiplier = fontSizeMultiplier
        ) {
            val appViewModel: AppViewModel = koinViewModel()
            val navigationState = koinInject<FloconNavigationState>()
            val leftPanelState by appViewModel.leftPanelState.collectAsStateWithLifecycle()
            var expanded by remember { mutableStateOf(true) }
            val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)
            var windowSize by remember { mutableStateOf(IntSize.Zero) }
            val position by animateDpAsState(
                targetValue = if (expanded) PanelMaxWidth else PanelMinWidth,
            )
            val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

            FloconSurface(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
            ) {
                CompositionLocalProvider(LocalFloconPanelController provides panelController) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MainScreen(
                            modifier = Modifier
                                .fillMaxSize(),
                        )
                        FloconPanelDisplayer(
                            panelController = panelController,
                            modifier = Modifier.fillMaxSize()
                        )
                        FeedbackDisplayerView()
                        VersionCheckerView()
                    }
                }
            }

            FloconSurface(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        windowSize = it.size // TODO Add windowsize lib
                    },
            ) {
                FloconNavigation(
                    navigationState = navigationState,
                    sceneStrategy = MenuSceneStrategy(
                        menuContent = {
                            LeftPanelView(
                                state = leftPanelState,
                                onClickItem = {},
                                modifier = Modifier
                                    .width(width)
                                    .fillMaxHeight(),
                                expanded = expanded
                            )
                        },
                        expander = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(60.dp)
                                    .graphicsLayer {
                                        translationX = position.toPx() - size.width / 2 - 8.dp.toPx()
                                        translationY = (windowSize.height / 2) - (size.height / 2)
                                    }
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FloconTheme.colorPalette.primary)
                                    .clickable(onClick = { expanded = !expanded }),
                            ) {
                                FloconIcon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    tint = Color.LightGray,
                                    modifier = Modifier.rotate(rotate),
                                )
                            }
                        }
                    )
                        .then(SinglePaneSceneStrategy()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    networkNavigation()
                }
            }
//            Box(
//                Modifier
//                    .safeContentPadding()
//                    .fillMaxSize()
//                    .background(FloconTheme.colorPalette.background),
//            ) {
//                MainScreen(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                )
//                FeedbackDisplayerView()
//            }
        }
    }
}
