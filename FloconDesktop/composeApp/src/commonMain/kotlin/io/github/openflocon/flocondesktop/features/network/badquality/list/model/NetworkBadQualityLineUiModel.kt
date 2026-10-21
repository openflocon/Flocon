package io.github.openflocon.flocondesktop.features.network.badquality.list.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class NetworkBadQualityLineUiModel(
    val id: String,
    val isEnabled: Boolean,
    val name: String,
    val trigger: String = "", // ex: "100%"
    val latency: String = "", // ex: "50 - 150 ms"
    val errorRate: String = "", // ex: "100%"
    val errors: ImmutableList<ErrorUi> = persistentListOf(),
) {
    @Immutable
    data class ErrorUi(
        val label: String, // a http code or the name of an exception
        val isException: Boolean,
    )
}

fun previewNetworkBadQualityLineUiModel() = NetworkBadQualityLineUiModel(
    id = "1",
    isEnabled = true,
    name = "the name",
    trigger = "100%",
    latency = "50 - 150 ms",
    errorRate = "0%",
)
