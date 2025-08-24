package io.github.openflocon.flocondesktop.features.table.mapper

import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel

fun TableIdentifierDomainModel.toUi() = DeviceTableUiModel(
    id = id,
    name = name,
)
