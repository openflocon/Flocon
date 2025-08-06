package io.github.openflocon.flocondesktop.features.network.ui.model.header

import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi

sealed interface OnFilterAction {
    data class ClickOnMethod(val methodUi: NetworkMethodUi) : OnFilterAction
}
