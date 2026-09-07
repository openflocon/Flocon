package io.github.openflocon.flocondesktop.features.network.body

import androidx.lifecycle.ViewModel
import io.github.openflocon.flocondesktop.features.network.body.model.NetworkBodyDetailUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkJsonViewModel(
    json: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkBodyDetailUi(text = json))
    val uiState: StateFlow<NetworkBodyDetailUi> = _uiState.asStateFlow()

}
