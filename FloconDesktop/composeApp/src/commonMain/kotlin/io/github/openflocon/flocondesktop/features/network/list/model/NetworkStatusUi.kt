package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkStatusUi(
    val text: String,
    val status: Status,
) {
    enum class Status {
        SUCCESS,
        ERROR,
        EXCEPTION,
        LOADING,
    }
}
