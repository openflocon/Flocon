package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class NetworkMethodUi(
    open val text: String,
) {
    data object GET : NetworkMethodUi(text = "GET")

    data object POST : NetworkMethodUi(text = "POST")

    data object PUT : NetworkMethodUi(text = "PUT")

    data object DELETE : NetworkMethodUi(text = "DELETE")

    data class OTHER(
        override val text: String,
    ) : NetworkMethodUi(text = text)
}
