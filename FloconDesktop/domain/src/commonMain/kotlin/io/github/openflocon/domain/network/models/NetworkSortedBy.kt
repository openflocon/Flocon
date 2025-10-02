package io.github.openflocon.domain.network.models

data class NetworkSortedBy(
    val column: Column,
    val asc: Boolean,
) {
    enum class Column {
        RequestStartTimeFormatted,
        Method,
        Domain, // todo parse before
        Query, // TODO parse before
        Status, // todo parse before,
        DurationFormatted
    }
}
