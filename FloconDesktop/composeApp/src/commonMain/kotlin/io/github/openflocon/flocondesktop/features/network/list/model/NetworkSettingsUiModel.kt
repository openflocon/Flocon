package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkSettingsUiModel(
    val displayOldSessions: Boolean,
    val autoScroll: Boolean,
    val invertList: Boolean
)

fun previewNetworkSettingsUiModel() = NetworkSettingsUiModel(
    displayOldSessions = true,
    autoScroll = false,
    invertList = false,
)
