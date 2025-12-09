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
