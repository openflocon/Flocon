package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterStateUiModel.FilterItem

@Immutable
data class TextFilterStateUiModel(
    val includedFilters: List<FilterItem>,
    val excludedFilters: List<FilterItem>,
    val isEnabled: Boolean,
) : FilterState {

    val allFilters = includedFilters + excludedFilters

    @Immutable
    data class FilterItem(
        val text: String,
        val isActive: Boolean,
        val isExcluded: Boolean,
    )

    override val isActive: Boolean = allFilters.isNotEmpty() && isEnabled && allFilters.any { it.isActive }

    companion object Companion {
        val EMPTY = TextFilterStateUiModel(
            includedFilters = emptyList(),
            excludedFilters = emptyList(),
            isEnabled = false,
        )
    }
}

fun previewTextFilterState() = TextFilterStateUiModel(
    isEnabled = true,
    includedFilters = listOf(
        FilterItem(text = "toInclude", isExcluded = false, isActive = true),
        FilterItem(text = "toInclude2", isExcluded = false, isActive = true),
    ),
    excludedFilters = listOf(
        FilterItem(text = "toExclude", isExcluded = true, isActive = true),
        FilterItem(text = "toExclude2", isExcluded = true, isActive = false),
    ),
)
