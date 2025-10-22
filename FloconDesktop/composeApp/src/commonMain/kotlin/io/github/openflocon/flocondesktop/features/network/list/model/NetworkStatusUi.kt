package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
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
