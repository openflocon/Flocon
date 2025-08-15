package io.github.openflocon.flocondesktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flocon.data.remote.dataRemoteModule
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.domainModule
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
import org.koin.compose.KoinApplication
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
                dataLocalModule,
                dataRemoteModule,
                // Temporary
                module {
                    singleOf(::AdbRepositoryImpl) bind AdbRepository::class
                },
            )
        }
    ) {
        FloconTheme {
            val appViewModel: AppViewModel = koinViewModel()

            Box(
                Modifier
                    .safeContentPadding()
                    .fillMaxSize()
                    .background(FloconTheme.colorPalette.background),
            ) {
                MainScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                )
                FeedbackDisplayerView()
            }
        }
    }
}
