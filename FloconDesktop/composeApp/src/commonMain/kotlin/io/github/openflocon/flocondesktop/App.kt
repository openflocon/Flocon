package io.github.openflocon.flocondesktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.data.core.dataCoreModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
import io.github.openflocon.flocondesktop.adb.AdbRepositoryImpl
import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.flocondesktop.app.di.appModule
import io.github.openflocon.flocondesktop.common.di.commonModule
import io.github.openflocon.flocondesktop.core.di.coreModule
import io.github.openflocon.flocondesktop.features.featuresModule
import io.github.openflocon.flocondesktop.features.network.NetworkRoute
import io.github.openflocon.flocondesktop.features.network.networkNavigation
import io.github.openflocon.flocondesktop.main.di.mainModule
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.navigation.FloconNavigation
import io.github.openflocon.navigation.FloconNavigationState
import io.github.openflocon.navigation.navigationModule
import kotlinx.coroutines.delay
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@Composable
fun App() {
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
        FloconTheme {
            val appViewModel: AppViewModel = koinViewModel()
            val navigationState = koinInject<FloconNavigationState>()

            LaunchedEffect(Unit) {
                navigationState.navigate(NetworkRoute)
            }

            FloconSurface(
                modifier = Modifier.fillMaxSize()
            ) {
                FloconNavigation(
                    navigationState = navigationState,
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
