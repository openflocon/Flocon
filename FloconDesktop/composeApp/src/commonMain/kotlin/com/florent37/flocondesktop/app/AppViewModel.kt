package com.florent37.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florent37.flocondesktop.core.domain.settings.InitAdbPathUseCase
import com.florent37.flocondesktop.core.domain.settings.StartAdbForwardUseCase
import com.florent37.flocondesktop.messages.ui.MessagesServerDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppViewModel(
    messagesServerDelegate: MessagesServerDelegate,
    initAdbPathUseCase: InitAdbPathUseCase,
    startAdbForwardUseCase: StartAdbForwardUseCase,
) : ViewModel(
    messagesServerDelegate,
) {
    init {
        initAdbPathUseCase()
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
