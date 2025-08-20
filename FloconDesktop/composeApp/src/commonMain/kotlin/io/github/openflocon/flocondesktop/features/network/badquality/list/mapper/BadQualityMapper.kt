package io.github.openflocon.flocondesktop.features.network.badquality.list.mapper

import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel

fun BadQualityConfigDomainModel.toLineUi() = NetworkBadQualityLineUiModel(
    id = id,
    name = name,
    isEnabled = isEnabled,
)
