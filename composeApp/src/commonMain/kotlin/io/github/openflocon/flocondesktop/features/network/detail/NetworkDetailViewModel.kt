package io.github.openflocon.flocondesktop.features.network.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.domain.network.usecase.CreateNetworkLogHtmlUseCase
import io.github.openflocon.domain.network.usecase.DecodeJwtTokenUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.core.data.settings.usecase.ObserveNetworkSettingsUseCase
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.network.detail.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.delegate.OpenBodyDelegate
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.flocondesktop.telemetry.TelemetryFeature
import io.github.openflocon.flocondesktop.telemetry.TelemetryManager
import io.github.openflocon.flocondesktop.telemetry.TelemetryScreen
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.library.designsystem.common.readFromClipboard
import io.github.openflocon.library.designsystem.common.saveImageToFile
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock

class NetworkDetailViewModel(
    private val requestId: String,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val navigationState: MainFloconNavigationState,
    private val observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val telemetryManager: TelemetryManager,
) : ViewModel(),
    KoinComponent {

    private val openBodyDelegate: OpenBodyDelegate by inject()
    private val networkItemOptionsDelegate: NetworkItemOptionsDelegate by inject()
    private val createNetworkLogHtmlUseCase: CreateNetworkLogHtmlUseCase by inject()
    private val observeNetworkSettingsUseCase: ObserveNetworkSettingsUseCase by inject()

    val uiState: StateFlow<NetworkDetailViewState> = combine(
        observeNetworkRequestsByIdUseCase(requestId)
            .distinctUntilChanged()
            .filterNotNull(),
        observeNetworkSettingsUseCase(),
    ) { call, settings ->
        call.toDetailUi(
            defaultSelectedTab = settings.defaultSelectedTab,
            decodeJwtPayload = decodeJwtTokenUseCase::invoke,
        )
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(
            NetworkDetailViewState(
                callId = "",
                fullUrl = "",
                requestTimeFormatted = "",
                durationFormatted = "",
                method = NetworkDetailViewState.Method.Http(NetworkMethodUi.Http.GET),
                statusLabel = "",
                status = NetworkStatusUi(
                    text = "",
                    status = NetworkStatusUi.Status.SUCCESS,
                ),
                graphQlSection = null,
                requestBodyTitle = "",
                requestBody = "",
                requestSize = "",
                requestHeaders = persistentListOf(),
                response = null,
                requestBodyIsNotBlank = false,
                canOpenRequestBody = false,
                imageUrl = null,
                imageHeaders = null,
                defaultSelectedTab = NetworkDetailTab.Request,
            )
        )

    init {
        // the panel opens for every clicked request (screenViewed is once per launch)
        telemetryManager.screenViewed(TelemetryScreen.NetworkDetail)
    }

    fun onAction(action: NetworkDetailAction) {
        // exhaustive: a new action does not compile without a decision. Only the kind of action, never its content.
        telemetryManager.featureUsed(
            when (action) {
                is NetworkDetailAction.CopyText -> TelemetryFeature.DetailCopyText
                is NetworkDetailAction.CopyImage -> TelemetryFeature.DetailCopyImage
                is NetworkDetailAction.SaveImage -> TelemetryFeature.DetailSaveImage
                is NetworkDetailAction.DisplayBearerJwt -> TelemetryFeature.DetailBearerJwt
                is NetworkDetailAction.JsonDetail -> TelemetryFeature.DetailJsonDetail
                is NetworkDetailAction.DiffWithClipboard -> TelemetryFeature.DetailDiffWithClipboard
                is NetworkDetailAction.OpenBodyExternally.Request -> TelemetryFeature.DetailOpenExternallyRequest
                is NetworkDetailAction.OpenBodyExternally.Response -> TelemetryFeature.DetailOpenExternallyResponse
                is NetworkDetailAction.ShareAsMarkdown -> TelemetryFeature.DetailShareMarkdown
                is NetworkDetailAction.CopyCurl -> TelemetryFeature.DetailCopyCurl
                is NetworkDetailAction.SaveAsHtml -> TelemetryFeature.DetailSaveHtml
            },
        )
        when (action) {
            is NetworkDetailAction.CopyText -> onCopyText(action)
            is NetworkDetailAction.CopyImage -> onCopyImage(action)
            is NetworkDetailAction.SaveImage -> onSaveImage(action)
            is NetworkDetailAction.DisplayBearerJwt -> displayBearerJwt(action.token)
            is NetworkDetailAction.JsonDetail -> onJsonDetail(action)
            is NetworkDetailAction.DiffWithClipboard -> onDiffWithClipboard(action)
            is NetworkDetailAction.OpenBodyExternally.Request -> openBodyDelegate.openBodyExternally(action.item)
            is NetworkDetailAction.OpenBodyExternally.Response -> openBodyDelegate.openBodyExternally(action.item)
            is NetworkDetailAction.ShareAsMarkdown -> onOption(NetworkItemOption.ShareAsMarkdown)
            is NetworkDetailAction.CopyCurl -> onOption(NetworkItemOption.CopyCurl)
            is NetworkDetailAction.SaveAsHtml -> onSaveHtml()
        }
    }

    private fun onSaveHtml() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            createNetworkLogHtmlUseCase(requestId)
        }
    }

    private fun onCopyText(action: NetworkDetailAction.CopyText) {
        copyToClipboard(action.text)
        viewModelScope.launch(dispatcherProvider.viewModel) {
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }

    private fun onCopyImage(action: NetworkDetailAction.CopyImage) {
        copyToClipboard(action.bitmap)
        viewModelScope.launch(dispatcherProvider.viewModel) {
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }

    private fun onSaveImage(action: NetworkDetailAction.SaveImage) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val time = Clock.System.now().epochSeconds
            val id = requestId.takeIf { it.isNotBlank() } ?: System.currentTimeMillis().toString()
            val saved = saveImageToFile(action.bitmap, defaultFileName = "network_${id}_$time.png")
            if (saved) {
                feedbackDisplayer.displayMessage("Image saved successfully")
            }
        }
    }

    private fun onJsonDetail(action: NetworkDetailAction.JsonDetail) {
        navigationState.navigate(NetworkRoutes.JsonDetail(action.json))
    }

    private fun onDiffWithClipboard(action: NetworkDetailAction.DiffWithClipboard) {
        val clipboardJson = readFromClipboard()
        navigationState.navigate(
            NetworkRoutes.Diff(
                json = action.text,
                clipboardJson = clipboardJson.orEmpty()
            )
        )
    }

    private fun displayBearerJwt(token: String) {
        decodeJwtTokenUseCase(token)?.let {
            onJsonDetail(NetworkDetailAction.JsonDetail(json = it))
        }
    }

    private fun onOption(option: NetworkItemOption) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            networkItemOptionsDelegate.onOption(callId = requestId, option = option)
        }
    }
}
