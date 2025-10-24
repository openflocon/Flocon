package io.github.openflocon.domain.models.settings

data class NetworkSettings(
    val pinnedDetails: Boolean,
    val displayOldSessions: Boolean,
    val autoScroll: Boolean,
    val invertList: Boolean
)
