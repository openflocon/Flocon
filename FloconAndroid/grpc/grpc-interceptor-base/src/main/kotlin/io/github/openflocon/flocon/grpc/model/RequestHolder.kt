package io.github.openflocon.flocon.grpc.model

import io.github.openflocon.flocon.network.core.model.FloconNetworkRequest
import kotlinx.coroutines.CompletableDeferred

internal data class RequestHolder(
    val request: CompletableDeferred<FloconNetworkRequest> = CompletableDeferred()
)