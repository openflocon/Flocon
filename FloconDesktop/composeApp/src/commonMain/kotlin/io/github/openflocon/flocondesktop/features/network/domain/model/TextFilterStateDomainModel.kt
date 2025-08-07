package io.github.openflocon.flocondesktop.features.network.domain.model

data class TextFilterStateDomainModel(
    val items: List<FilterItem>,
    val isEnabled: Boolean,
) {
    data class FilterItem(
        val text: String,
        val isActive: Boolean,
        val isExcluded: Boolean,
    )
}
