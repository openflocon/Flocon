package io.github.openflocon.domain.network.models

data class NetworkFilterDomainModel(
    val filterOnAllColumns: String?,
    val filters: List<Filters>?,
) {
    data class Filters(
        val column: NetworkTextFilterColumns,
        val includedFilters: List<FilterItem>,
        val excludedFilters: List<FilterItem>,
    ) {
        data class FilterItem(
            val text: String,
        )
    }
}
