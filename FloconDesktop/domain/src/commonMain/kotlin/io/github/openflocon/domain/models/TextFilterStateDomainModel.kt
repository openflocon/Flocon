package io.github.openflocon.domain.models

data class TextFilterStateDomainModel(
    val items: List<FilterItem>,
    val isEnabled: Boolean,
) {
    data class FilterItem(
        val text: String,
        val isActive: Boolean,
        val isExcluded: Boolean,
        val isRegex: Boolean,
    )
}
