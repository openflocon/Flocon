package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkStatusUi(
    val code: Int,
    val isSuccess: Boolean,
)
