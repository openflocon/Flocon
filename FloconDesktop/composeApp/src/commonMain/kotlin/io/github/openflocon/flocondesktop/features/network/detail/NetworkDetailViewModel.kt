package io.github.openflocon.flocondesktop.features.network.detail

import androidx.lifecycle.ViewModel

// TODO: Remove delegate
class NetworkDetailViewModel(
    requestId: String,
    private val delegate: NetworkDetailDelegate
) : ViewModel(delegate) {

    val uiState = delegate.uiState

    init {
        delegate.setRequestId(requestId)
    }

    fun onAction(action: NetworkDetailAction) {
        delegate.onAction(action)
    }
}
