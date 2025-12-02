package io.github.openflocon.flocondesktop.features.network.detail

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class NetworkDetailViewModel(
    requestId: String,
    private val delegate: NetworkDetailDelegate
) : ViewModel(),
    KoinComponent {

    val uiState = delegate.uiState

    init {
        delegate.setRequestId(requestId)
    }

    fun onAction(action: NetworkDetailAction) {
        delegate.onAction(action)
    }
}
