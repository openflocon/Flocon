package io.github.openflocon.domain.database.models

data class FilterQueryLogDomainModel(
    val text: String,
    val type: FilterType,
) {
    enum class FilterType {
        INCLUDE,
        EXCLUDE
    }
}