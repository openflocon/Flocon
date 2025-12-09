package io.github.openflocon.domain.network.usecase.search

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.domain.network.models.responseHeaders

class SearchNetworkCallsUseCase(
    private val getAllNetworkRequestsUseCase: GetAllNetworkRequestsUseCase
) {
    suspend operator fun invoke(
        query: String,
        scope: Set<SearchScope>
    ): List<FloconNetworkCallDomainModel> {
        if (query.isBlank() || scope.isEmpty()) {
            return emptyList()
        }

        val calls = getAllNetworkRequestsUseCase()

        return calls.filter { call ->
            scope.any { s ->
                when (s) {
                    SearchScope.RequestHeader -> call.request.headers.any {
                        it.key.contains(query, ignoreCase = true) || it.value.contains(
                            query,
                            ignoreCase = true
                        )
                    }

                    SearchScope.RequestBody -> call.request.body?.contains(query, ignoreCase = true)
                        ?: false

                    SearchScope.ResponseHeader -> call.responseHeaders()?.any {
                        it.key.contains(query, ignoreCase = true) || it.value.contains(
                            query,
                            ignoreCase = true
                        )
                    } == true

                    SearchScope.ResponseBody -> call.responseBody()
                        ?.contains(query, ignoreCase = true) == true

                    SearchScope.Url -> call.request.url.contains(query, ignoreCase = true)
                    SearchScope.Method -> call.request.method.contains(query, ignoreCase = true)
                    SearchScope.Status -> call.response?.statusFormatted?.contains(
                        query,
                        ignoreCase = true
                    ) == true
                }
            }
        }
    }
}
