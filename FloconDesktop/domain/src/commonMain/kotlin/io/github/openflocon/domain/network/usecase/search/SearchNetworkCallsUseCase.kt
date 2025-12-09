package io.github.openflocon.domain.network.usecase.search

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchNetworkCallsUseCase(
    private val observeNetworkRequestsUseCase: ObserveNetworkRequestsUseCase
) {
    operator fun invoke(
        query: String,
        scope: Set<SearchScope>
    ): Flow<List<FloconNetworkCallDomainModel>> {
        return observeNetworkRequestsUseCase().map { calls ->
            if (query.isBlank() || scope.isEmpty()) {
                emptyList()
            } else {
                calls.filter { call ->
                    scope.any { s ->
                        when (s) {
                            SearchScope.RequestHeader -> call.request.headers.any {
                                it.key.contains(query, ignoreCase = true) || it.value.contains(query, ignoreCase = true)
                            }
                            SearchScope.RequestBody -> call.request.body.contains(query, ignoreCase = true)
                            SearchScope.ResponseHeader -> call.response?.headers?.any {
                                it.key.contains(query, ignoreCase = true) || it.value.contains(query, ignoreCase = true)
                            } == true
                            SearchScope.ResponseBody -> call.response?.body?.contains(query, ignoreCase = true) == true
                            SearchScope.Url -> call.request.url.contains(query, ignoreCase = true)
                            SearchScope.Method -> call.request.method.contains(query, ignoreCase = true)
                            SearchScope.Status -> call.response?.code?.toString()?.contains(query, ignoreCase = true) == true
                        }
                    }
                }
            }
        }
    }
}
