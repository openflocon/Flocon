package io.github.openflocon.flocondesktop.features.network.options

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.network_copy_curl
import flocondesktop.composeapp.generated.resources.network_copy_url
import flocondesktop.composeapp.generated.resources.network_create_mock
import flocondesktop.composeapp.generated.resources.network_replay
import flocondesktop.composeapp.generated.resources.network_share_markdown
import io.github.openflocon.library.designsystem.common.FloconContextMenuBuilder
import org.jetbrains.compose.resources.stringResource

// the labels are resolved in the composition: a menu is built outside of it
@Immutable
class NetworkItemOptionLabels(
    private val copyUrl: String,
    private val copyCurl: String,
    private val createMock: String,
    private val replay: String,
    private val shareAsMarkdown: String,
) {
    fun of(option: NetworkItemOption): String = when (option) {
        NetworkItemOption.CopyUrl -> copyUrl
        NetworkItemOption.CopyCurl -> copyCurl
        NetworkItemOption.CreateMock -> createMock
        NetworkItemOption.Replay -> replay
        NetworkItemOption.ShareAsMarkdown -> shareAsMarkdown
    }
}

@Composable
fun rememberNetworkItemOptionLabels(): NetworkItemOptionLabels {
    val copyUrl = stringResource(Res.string.network_copy_url)
    val copyCurl = stringResource(Res.string.network_copy_curl)
    val createMock = stringResource(Res.string.network_create_mock)
    val replay = stringResource(Res.string.network_replay)
    val shareAsMarkdown = stringResource(Res.string.network_share_markdown)
    return remember(copyUrl, copyCurl, createMock, replay, shareAsMarkdown) {
        NetworkItemOptionLabels(copyUrl, copyCurl, createMock, replay, shareAsMarkdown)
    }
}

/**
 * The options of a network call as items of a menu, the same in every screen that shows the call.
 */
fun FloconContextMenuBuilder.networkItemOptions(
    labels: NetworkItemOptionLabels,
    httpActions: Boolean,
    onOption: (NetworkItemOption) -> Unit,
) {
    networkItemOptions(httpActions).forEach { option ->
        item(label = labels.of(option), onClick = { onOption(option) })
    }
}
