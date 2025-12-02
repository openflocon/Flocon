package io.github.openflocon.flocondesktop.features.images.mapper

import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import kotlinx.collections.immutable.toPersistentMap

internal fun List<DeviceImageDomainModel>.filterBy(filter: String): List<DeviceImageDomainModel> = if (filter.isEmpty()) {
    this
} else this.filter {
    it.url.contains(filter)
}

internal fun DeviceImageDomainModel.toUi(): ImagesUiModel = ImagesUiModel(
    url = url,
    downloadedAt = formatTimestamp(time),
    headers = headers.toPersistentMap(),
)
