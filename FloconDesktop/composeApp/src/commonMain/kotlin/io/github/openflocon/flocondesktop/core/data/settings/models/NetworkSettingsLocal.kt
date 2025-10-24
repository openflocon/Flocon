package io.github.openflocon.flocondesktop.core.data.settings.models

import io.github.openflocon.domain.models.settings.NetworkSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkSettingsLocal(

    @SerialName("pinned_detail")
    val pinnedDetails: Boolean = false,

    @SerialName("display_old_sessions")
    val displayOldSessions: Boolean = false,

    @SerialName("auto_scroll")
    val autoScroll: Boolean = false,

    @SerialName("invert_list")
    val invertList: Boolean = false

)

internal fun NetworkSettingsLocal.toDomain() = NetworkSettings(
    pinnedDetails = pinnedDetails,
    displayOldSessions = displayOldSessions,
    autoScroll = autoScroll,
    invertList = invertList
)

internal fun NetworkSettings.toLocal() = NetworkSettingsLocal(
    pinnedDetails = pinnedDetails,
    displayOldSessions = displayOldSessions,
    autoScroll = autoScroll,
    invertList = invertList
)
