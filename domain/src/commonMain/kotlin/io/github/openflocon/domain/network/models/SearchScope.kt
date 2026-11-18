package io.github.openflocon.domain.network.models

enum class SearchScope {
    RequestHeader,
    RequestBody,
    ResponseHeader,
    ResponseBody,
    Url,
    Method,
    Status
}

fun SearchScope.text(): String = when (this) {
    SearchScope.RequestHeader -> "Request Header"
    SearchScope.RequestBody -> "Request Body"
    SearchScope.ResponseHeader -> "Response Header"
    SearchScope.ResponseBody -> "Response Body"
    SearchScope.Url -> "Url"
    SearchScope.Method -> "Method"
    SearchScope.Status -> "Status"
}
