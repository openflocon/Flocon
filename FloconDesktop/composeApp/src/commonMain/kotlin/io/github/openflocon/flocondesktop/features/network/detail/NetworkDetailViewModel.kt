package io.github.openflocon.flocondesktop.features.network.detail

import androidx.lifecycle.ViewModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import org.koin.core.component.KoinComponent

class NetworkDetailViewModel(
    requestId: String,
    delegate: NetworkDetailDelegate
) : ViewModel(), KoinComponent {

    val uiState = delegate.uiState

    init {
        delegate.setRequestId(requestId)
    }

    fun onAction(action: NetworkAction) {

    }

}
