package io.github.openflocon.flocondesktop.features.network.options

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.domain.network.usecase.GetNetworkCallAsMarkdownUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import io.github.openflocon.domain.network.usecase.ReplayNetworkCallUseCase
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.compose.resources.getString

/**
 * What the options of a network call do, shared by the network list, the network detail and the timeline.
 * The caller gives the scope: the options are suspend functions, launched from the viewModel that displays the call.
 */
class NetworkItemOptionsDelegate(
    private val observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
    private val generateCurlCommandUseCase: GenerateCurlCommandUseCase,
    private val getNetworkCallAsMarkdownUseCase: GetNetworkCallAsMarkdownUseCase,
    private val replayNetworkCallUseCase: ReplayNetworkCallUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val navigationState: MainFloconNavigationState,
) {

    suspend fun onOption(callId: String, option: NetworkItemOption) {
        when (option) {
            NetworkItemOption.CopyUrl -> copyUrl(callId)
            NetworkItemOption.CopyCurl -> copyCurl(callId)
            NetworkItemOption.ShareAsMarkdown -> copyMarkdown(callId)
            NetworkItemOption.Replay -> replayNetworkCallUseCase(callId)
            NetworkItemOption.CreateMock -> navigationState.navigate(NetworkRoutes.Mocks(callId))
        }
    }

    private suspend fun copyUrl(callId: String) {
        val call = observeNetworkRequestsByIdUseCase(callId).firstOrNull() ?: return
        copy(call.request.url)
    }

    private suspend fun copyCurl(callId: String) {
        val call = observeNetworkRequestsByIdUseCase(callId).firstOrNull() ?: return
        copy(generateCurlCommandUseCase(call))
    }

    private suspend fun copyMarkdown(callId: String) {
        copy(getNetworkCallAsMarkdownUseCase(callId) ?: return)
    }

    private suspend fun copy(text: String) {
        copyToClipboard(text)
        feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
    }
}
