package io.github.openflocon.domain.network.usecase.search

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.domain.network.models.responseHeaders

fun FloconNetworkCallDomainModel.matches(
    query: String,
    scope: Set<SearchScope>,
): Boolean = scope.any { matches(query = query, scope = it) }

fun FloconNetworkCallDomainModel.matches(
    query: String,
    scope: SearchScope,
): Boolean = when (scope) {
    SearchScope.RequestHeader -> request.headers.any {
        it.key.contains(query, ignoreCase = true) || it.value.contains(
            query,
            ignoreCase = true
        )
    }

    SearchScope.RequestBody -> request.body?.contains(query, ignoreCase = true)
        ?: false

    SearchScope.ResponseHeader -> responseHeaders()?.any {
        it.key.contains(query, ignoreCase = true) || it.value.contains(
            query,
            ignoreCase = true
        )
    } == true

    SearchScope.ResponseBody -> responseBody()
        ?.contains(query, ignoreCase = true) == true

    SearchScope.Url -> request.url.contains(query, ignoreCase = true)
    SearchScope.Method -> request.method.contains(query, ignoreCase = true)
    SearchScope.Status -> response?.statusFormatted?.contains(
        query,
        ignoreCase = true
    ) == true
}
