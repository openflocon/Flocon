package io.github.openflocon.domain.network.models

data class NetworkSortedBy(
    val column: Column,
    val asc: Boolean,
) {
    enum class Column {
        RequestStartTimeFormatted,
        Method,
        Domain,
        Query,
        Status,
        Duration
    }
}
