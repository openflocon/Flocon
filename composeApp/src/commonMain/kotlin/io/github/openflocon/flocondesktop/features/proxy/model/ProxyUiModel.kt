package io.github.openflocon.flocondesktop.features.proxy.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ProxyUiState(
    val enabled: Boolean,
    val status: ProxyStatusUiModel,
    /** How the mascot and the code rain look, following the status. */
    val mood: ProxyMoodUiModel,
    /** The mascot and the code rain can be turned off in the settings. */
    val mascotEnabled: Boolean,
)

@Immutable
enum class ProxyMoodUiModel {
    /** Running: the code rains, the mascot watches. */
    Watching,

    /** Off or starting: the rain is frozen, the mascot sleeps. */
    Asleep,

    /** Failed: the rain turns red, the shades slip. */
    Glitched,
}

@Immutable
sealed interface ProxyStatusUiModel {
    data object Off : ProxyStatusUiModel
    data object Starting : ProxyStatusUiModel
    data object BinaryNotFound : ProxyStatusUiModel
    data class Failed(val message: String) : ProxyStatusUiModel

    /** One address per network of the computer: the phone uses the one of its Wi-Fi. */
    data class Running(val addresses: ImmutableList<ProxyAddressUiModel>) : ProxyStatusUiModel
}

@Immutable
data class ProxyAddressUiModel(
    val ip: String,
    val proxyPort: String,
    /** Page to open on the phone to install the Flocon CA. */
    val caUrl: String,
)

fun previewProxyUiState(status: ProxyStatusUiModel = previewRunningProxyStatus()) = ProxyUiState(
    enabled = status != ProxyStatusUiModel.Off,
    status = status,
    mood = when (status) {
        is ProxyStatusUiModel.Running -> ProxyMoodUiModel.Watching
        ProxyStatusUiModel.BinaryNotFound, is ProxyStatusUiModel.Failed -> ProxyMoodUiModel.Glitched
        else -> ProxyMoodUiModel.Asleep
    },
    mascotEnabled = true,
)

fun previewRunningProxyStatus() = ProxyStatusUiModel.Running(
    addresses = persistentListOf(
        ProxyAddressUiModel(ip = "192.168.1.20", proxyPort = "8080", caUrl = "http://192.168.1.20:8081/"),
        ProxyAddressUiModel(ip = "10.0.0.12", proxyPort = "8080", caUrl = "http://10.0.0.12:8081/"),
    ),
)
