package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.RestartAppUseCase
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.menu.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.menu.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class AppViewModel(
    messagesServerDelegate: MessagesServerDelegate,
    initAdbPathUseCase: InitAdbPathUseCase,
    startAdbForwardUseCase: StartAdbForwardUseCase,
    val navigationState: MainFloconNavigationState,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    dispatcherProvider: DispatcherProvider,
    private val devicesDelegate: DevicesDelegate,
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val restartAppUseCase: RestartAppUseCase,
    private val recordVideoDelegate: RecordVideoDelegate,
    private val feedbackDisplayer: FeedbackDisplayer
) : ViewModel(messagesServerDelegate) {

    private val contentState = MutableStateFlow(
        ContentUiState(
            current = SubScreen.Network
        )
    )

    val uiState = combine(
        contentState,
        devicesDelegate.devicesState,
        devicesDelegate.appsState,
        recordVideoDelegate.state
    ) { content, devices, apps, record ->
        AppUiState(
            contentState = content,
            deviceState = devices,
            appState = apps,
            recordState = record
        )
    }
        .stateInWhileSubscribed(
            AppUiState(
                contentState = contentState.value,
                deviceState = devicesDelegate.devicesState.value,
                appState = devicesDelegate.appsState.value,
                recordState = recordVideoDelegate.state.value
            )
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            initAdbPathUseCase().alsoFailure {
                initialSetupStateHolder.setRequiresInitialSetup()
            }

            messagesServerDelegate.initialize()

            launch {
                while (isActive) {
                    // ensure we have the forward enabled
                    startAdbForwardUseCase()
                    delay(1_500)
                }
            }
        }
    }

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.SelectMenu -> onSelectMenu(action)
        }
    }

    private fun onSelectMenu(action: AppAction.SelectMenu) {
        contentState.update { it.copy(current = action.menu) }
        navigationState.menu(
            when (action.menu) {
                SubScreen.Analytics -> AnalyticsRoutes.Main
                SubScreen.Dashboard -> TODO()
                SubScreen.Database -> TODO()
                SubScreen.Deeplinks -> TODO()
                SubScreen.Files -> TODO()
                SubScreen.Images -> TODO()
                SubScreen.Network -> NetworkRoutes.Main
                SubScreen.Settings -> TODO()
                SubScreen.SharedPreferences -> TODO()
                SubScreen.Tables -> TODO()
            }
        )
    }

}
