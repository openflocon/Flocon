package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.main.ui.buildLeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.flocondesktop.main.ui.model.id
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppViewModel(
    messagesServerDelegate: MessagesServerDelegate,
    initAdbPathUseCase: InitAdbPathUseCase,
    startAdbForwardUseCase: StartAdbForwardUseCase,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel(
    messagesServerDelegate,
) {

    val subScreen = MutableStateFlow(SubScreen.Network)
    val leftPanelState = subScreen.map { subScreen ->
        buildLeftPanelState(
            selectedId = subScreen.id,
        )
    }
        .flowOn(dispatcherProvider.ui)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = buildLeftPanelState(subScreen.value.id),
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            initAdbPathUseCase().alsoFailure {
                initialSetupStateHolder.setRequiresInitialSetup()
            }
        }

        messagesServerDelegate.initialize()

        viewModelScope.launch {
            while (isActive) {
                // ensure we have the forward enabled
                startAdbForwardUseCase()
                delay(1_500)
            }
        }
    }
}
