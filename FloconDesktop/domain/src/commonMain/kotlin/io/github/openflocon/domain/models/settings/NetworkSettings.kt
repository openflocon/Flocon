package io.github.openflocon.domain.models.settings

enum class NetworkDetailTab {
    Request,
    Response
}

data class NetworkSettings(
    val pinnedDetails: Boolean,
    val displayOldSessions: Boolean,
    val autoScroll: Boolean,
    val invertList: Boolean,
    val defaultSelectedTab: NetworkDetailTab = NetworkDetailTab.Request,
)

