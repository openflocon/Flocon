package io.github.openflocon.flocondesktop.features.connect.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class LocalAddressUiModel(
    val interfaceName: String,
    val ip: String,
)

@Immutable
data class ConnectUiState(
    val addresses: ImmutableList<LocalAddressUiModel>,
    val port: Int,
    val mascotEnabled: Boolean,
)

fun previewConnectUiState() = ConnectUiState(
    addresses = persistentListOf(
        LocalAddressUiModel(interfaceName = "en0", ip = "192.168.1.20"),
        LocalAddressUiModel(interfaceName = "en5", ip = "10.0.0.12"),
        LocalAddressUiModel(interfaceName = "bridge0", ip = "172.20.10.4"),
    ),
    port = 9023,
    mascotEnabled = true,
)

fun previewSingleConnectUiState() = previewConnectUiState().let { it.copy(addresses = it.addresses.take(1).toImmutableList()) }
