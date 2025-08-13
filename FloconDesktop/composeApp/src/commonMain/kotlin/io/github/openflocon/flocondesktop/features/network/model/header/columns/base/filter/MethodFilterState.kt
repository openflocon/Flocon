package io.github.openflocon.flocondesktop.features.network.model.header.columns.base.filter

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.model.NetworkMethodUi

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

    // here the logo should be active only if we removed one item from the list
    override val isActive: Boolean = items.any { !it.isSelected }
}

fun previewMethodFilterState() = MethodFilterState(
    isEnabled = true,
    items = NetworkMethodUi.Companion.all().mapIndexed { index, method ->
        MethodFilterState.Item(
            method = method,
            isSelected = index < 3,
        )
    },
)
