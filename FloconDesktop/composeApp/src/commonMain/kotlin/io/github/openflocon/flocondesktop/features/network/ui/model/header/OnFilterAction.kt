package io.github.openflocon.flocondesktop.features.network.ui.model.header

import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterColumns

sealed interface OnFilterAction {
    data class ClickOnMethod(val methodUi: NetworkMethodUi) : OnFilterAction
    data class TextFilter(val column: TextFilterColumns, val action: TextFilterAction) : OnFilterAction
}
