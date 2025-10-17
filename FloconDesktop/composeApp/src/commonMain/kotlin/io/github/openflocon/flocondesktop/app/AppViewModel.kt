package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.app.models.AppUiState
import io.github.openflocon.flocondesktop.app.models.route
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.network.NetworkRoute
import io.github.openflocon.flocondesktop.main.ui.buildLeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import io.github.openflocon.navigation.FloconNavigationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class AppViewModel(
    messagesServerDelegate: MessagesServerDelegate,
    initAdbPathUseCase: InitAdbPathUseCase,
    startAdbForwardUseCase: StartAdbForwardUseCase,
    val navigationState: FloconNavigationState,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    dispatcherProvider: DispatcherProvider,
) : ViewModel(messagesServerDelegate) {

    private val menuState = MutableStateFlow(
        buildLeftPanelState(current = SubScreen.Network)
    )

    val uiState = menuState.map {
        AppUiState(menuState = it)
    }
        .stateInWhileSubscribed(AppUiState(menuState = menuState.value))

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

            navigationState.navigate(NetworkRoute)
        }
    }

    fun onAction(action: AppAction) = when (action) {
        is AppAction.SelectMenu -> onSelectMenu(action)
    }

    private fun onSelectMenu(action: AppAction.SelectMenu) {
        menuState.update { it.copy(current = action.screen) }
        navigationState.navigate(action.screen.route)
    }

}
