package io.github.openflocon.flocondesktop.features.network.detail

import androidx.lifecycle.ViewModel
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
