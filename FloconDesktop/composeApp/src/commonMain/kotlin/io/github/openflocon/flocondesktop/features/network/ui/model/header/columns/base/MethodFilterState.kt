package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable

@Immutable
data class MethodFilterState(
    val isEnabled: Boolean,
) : FilterState {


    override val isActive: Boolean = isEnabled
}
