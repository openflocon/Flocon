package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkSettingsUiModel

fun NetworkSettings.toUi() = NetworkSettingsUiModel(
    pinPanel = pinnedDetails,
    displayOldSessions = displayOldSessions,
    autoScroll = autoScroll,
    invertList = invertList
)
