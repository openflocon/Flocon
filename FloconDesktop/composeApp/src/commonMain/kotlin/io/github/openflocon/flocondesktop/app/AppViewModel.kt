package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.core.domain.settings.InitAdbPathUseCase
import io.github.openflocon.flocondesktop.core.domain.settings.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

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

        // try to start the server
        // if fails -> try again in 3s
        // if success, just re-check again in 20s if it's still alive
        viewModelScope.launch(dispatcherProvider.viewModel) {
            while (isActive) {
                messagesServerDelegate.startServer().fold(
                    doOnSuccess = {
                        delay(20.seconds)
                    },
                    doOnFailure = {
                        delay(3.seconds)
                    }
                )
            }
        }


        viewModelScope.launch {
            while (isActive) {
                // ensure we have the forward enabled
                startAdbForwardUseCase()
                delay(1_500)
            }
        }
    }
}
