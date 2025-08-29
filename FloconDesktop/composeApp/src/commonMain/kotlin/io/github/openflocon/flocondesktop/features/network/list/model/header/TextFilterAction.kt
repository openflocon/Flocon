package io.github.openflocon.flocondesktop.features.network.list.model.header

import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel

sealed interface TextFilterAction {
    data class Include(val text: String, val isRegex: Boolean) : TextFilterAction
    data class Exclude(val text: String, val isRegex: Boolean) : TextFilterAction
    data class SetIsActive(val item: TextFilterStateUiModel.FilterItem, val isActive: Boolean) : TextFilterAction
    data class Delete(val item: TextFilterStateUiModel.FilterItem) : TextFilterAction
}
