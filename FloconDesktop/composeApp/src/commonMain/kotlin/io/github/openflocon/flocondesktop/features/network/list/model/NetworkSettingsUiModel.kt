package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.settings.NetworkDetailTab

@Immutable
data class NetworkSettingsUiModel(
    val displayOldSessions: Boolean,
    val autoScroll: Boolean,
    val invertList: Boolean,
    val pinPanel: Boolean,
    val defaultSelectedTab: NetworkDetailTab = NetworkDetailTab.Request,
)

fun previewNetworkSettingsUiModel() = NetworkSettingsUiModel(
    displayOldSessions = true,
    autoScroll = false,
    invertList = false,
    pinPanel = false,
    defaultSelectedTab = NetworkDetailTab.Request,
)

