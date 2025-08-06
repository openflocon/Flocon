package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi

@Immutable
data class MethodFilterState(
    val isEnabled: Boolean,
    val items: List<Item>,
) : FilterState {

    @Immutable
    data class Item(
        val method: NetworkMethodUi,
        val isSelected: Boolean,
    )

    override val isActive: Boolean = isEnabled
}
