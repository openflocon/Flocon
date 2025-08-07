package io.github.openflocon.flocondesktop.features.network.ui.model.header

import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState

sealed interface TextFilterAction {
    data class Include(val text: String) : TextFilterAction
    data class Exclude(val text: String) : TextFilterAction
    data class SetIsActive(val item: TextFilterState.FilterItem, val isActive: Boolean) : TextFilterAction
    data class Delete(val item: TextFilterState.FilterItem) : TextFilterAction
}
