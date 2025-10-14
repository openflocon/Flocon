package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkSettingsUiModel

fun NetworkSettingsDomainModel.toUi() = NetworkSettingsUiModel(
    displayOldSessions = displayOldSessions,
    autoScroll = autoScroll,
    invertList = invertList,
)
