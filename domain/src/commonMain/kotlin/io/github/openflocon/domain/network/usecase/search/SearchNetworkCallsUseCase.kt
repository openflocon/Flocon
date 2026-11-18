package io.github.openflocon.domain.network.usecase.search

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.SearchScope

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
            call.matches(query = query, scope = scope)
        }
    }
}
