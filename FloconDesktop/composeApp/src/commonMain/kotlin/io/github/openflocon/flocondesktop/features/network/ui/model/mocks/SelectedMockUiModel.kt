package io.github.openflocon.flocondesktop.features.network.ui.model.mocks

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SelectedMockUiModel {
    @Immutable
    data object Creation : SelectedMockUiModel

    @Immutable
    data class Edition(val existing: MockNetworkUiModel) : SelectedMockUiModel
}
