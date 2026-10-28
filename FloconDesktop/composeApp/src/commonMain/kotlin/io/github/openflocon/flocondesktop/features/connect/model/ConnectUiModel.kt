package io.github.openflocon.flocondesktop.features.connect.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class LocalAddressUiModel(
    val interfaceName: String,
    val ip: String,
)

@Immutable
data class ConnectUiState(
    val addresses: ImmutableList<LocalAddressUiModel>,
)

fun previewConnectUiState() = ConnectUiState(
    addresses = persistentListOf(
        LocalAddressUiModel(interfaceName = "en0", ip = "192.168.1.20"),
        LocalAddressUiModel(interfaceName = "en5", ip = "10.0.0.12"),
    ),
)
