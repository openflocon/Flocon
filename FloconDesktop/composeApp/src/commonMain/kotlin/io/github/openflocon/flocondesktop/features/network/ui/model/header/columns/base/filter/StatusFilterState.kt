package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter

import androidx.compose.runtime.Immutable

@Immutable
data class StatusFilterState(
    val isEnabled: Boolean,
) : FilterState {


    override val isActive: Boolean = isEnabled
}
