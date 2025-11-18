package io.github.openflocon.flocondesktop.features.network.detail

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.network.detail.mapper.toDetailUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class NetworkDetailDelegate(
    private val closeableDelegate: CloseableDelegate,
    observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
    dispatcherProvider: DispatcherProvider
) : CloseableScoped by closeableDelegate {

    private val _requestId = MutableStateFlow("")

    val uiState: StateFlow<NetworkDetailViewState> = _requestId.flatMapLatest {
        observeNetworkRequestsByIdUseCase(it)
    }
        .distinctUntilChanged()
        .filterNotNull()
        .map(FloconNetworkCallDomainModel::toDetailUi)
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
                requestHeaders = emptyList(),
                response = null,
                requestBodyIsNotBlank = false,
                canOpenRequestBody = false,
                imageUrl = null,
                imageHeaders = null,
            )
        )

    fun setRequestId(requestId: String) {
        _requestId.update { requestId }
    }

    fun onAction(action: NetworkAction) {

    }

}
