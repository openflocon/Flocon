package com.florent37.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.core.domain.settings.InitAdbPathUseCase
import com.florent37.flocondesktop.core.domain.settings.StartAdbForwardUseCase
import com.florent37.flocondesktop.messages.ui.MessagesServerDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
