package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter

import androidx.compose.runtime.Immutable

@Immutable
data class TextFilterState(
    val activeFilters: List<ActiveFilter>,
    val isEnabled: Boolean,
) : FilterState {
    @Immutable
    data class ActiveFilter(
        val text: String,
        val isExcluded: Boolean,
    )

    override val isActive: Boolean = activeFilters.isNotEmpty()
}
