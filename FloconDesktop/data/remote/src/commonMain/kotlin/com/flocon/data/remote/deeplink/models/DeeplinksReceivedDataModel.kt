package com.flocon.data.remote.deeplink.models

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinksReceivedDataModel(
    val deeplinks: List<DeeplinkReceivedDataModel>,
)

internal fun DeeplinksReceivedDataModel.toDomain(): List<DeeplinkDomainModel> = deeplinks.map {
    DeeplinkDomainModel(
        label = it.label,
        link = it.link,
        description = it.description,
    )
}
