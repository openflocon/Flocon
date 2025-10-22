package io.github.openflocon.flocondesktop.core.data.settings.models

import io.github.openflocon.domain.models.settings.NetworkSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkSettingsLocal(

    @SerialName("pinned_detail")
    val pinnedDetails: Boolean = false

)

internal fun NetworkSettingsLocal.toDomain() = NetworkSettings(
    pinnedDetails = pinnedDetails
)

internal fun NetworkSettings.toLocal() = NetworkSettingsLocal(
    pinnedDetails = pinnedDetails
)
