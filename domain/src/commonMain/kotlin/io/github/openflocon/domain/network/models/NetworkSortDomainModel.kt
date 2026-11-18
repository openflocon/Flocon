package io.github.openflocon.domain.network.models

data class NetworkSortDomainModel(
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
