@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.main.di.mainModule
import io.github.openflocon.flocondesktop.main.ui.MainScreen
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPanelDisplayer
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.LocalFloconPanelHandler
import io.github.openflocon.library.designsystem.components.rememberFloconPanelHandler
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
                    singleOf(::AdbRepositoryImpl) bind AdbRepository::class
                },
            )
        },
    ) {
        val fontSizeMultiplier by koinInject<ObserveFontSizeMultiplierUseCase>()()
            .collectAsStateWithLifecycle()

        val panelHandler = rememberFloconPanelHandler()

        FloconTheme(
            fontSizeMultiplier = fontSizeMultiplier
        ) {
            val appViewModel: AppViewModel = koinViewModel()

            FloconSurface(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
            ) {
                CompositionLocalProvider(LocalFloconPanelHandler provides panelHandler) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MainScreen(
                            modifier = Modifier
                                .fillMaxSize(),
                        )
                        FloconPanelDisplayer(
                            handler = panelHandler,
                            modifier = Modifier.fillMaxSize()
                        )
                        FeedbackDisplayerView()
                    }
                }
            }
        }
    }
}
