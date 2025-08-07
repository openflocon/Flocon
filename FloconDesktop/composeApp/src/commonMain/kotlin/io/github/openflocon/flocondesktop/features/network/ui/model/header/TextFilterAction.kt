package io.github.openflocon.flocondesktop.features.network.ui.model.header

import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterStateUiModel

sealed interface TextFilterAction {
    data class Include(val text: String) : TextFilterAction
    data class Exclude(val text: String) : TextFilterAction
    data class SetIsActive(val item: TextFilterStateUiModel.FilterItem, val isActive: Boolean) : TextFilterAction
    data class Delete(val item: TextFilterStateUiModel.FilterItem) : TextFilterAction
}
