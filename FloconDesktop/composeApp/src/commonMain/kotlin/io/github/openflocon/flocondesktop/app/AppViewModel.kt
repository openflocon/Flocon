package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.domain.settings.InitAdbPathUseCase
import io.github.openflocon.domain.settings.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import kotlinx.coroutines.delay
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
