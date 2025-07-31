package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkItemViewState(
    val uuid: String,
    val dateFormatted: String,
    val route: String,
    val method: NetworkMethodUi,
    val networkStatusUi: NetworkStatusUi,
    val requestSize: String,
    val responseSize: String,
    val timeFormatted: String,
)

fun previewNetworkItemViewState(): NetworkItemViewState = NetworkItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    route = "www.google.com.test",
    method = NetworkMethodUi.GET,
    networkStatusUi = NetworkStatusUi(200, true),
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
)
