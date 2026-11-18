package io.github.openflocon.flocondesktop.features.network.mock.edition.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SelectedMockUiModel {
    @Immutable
    data object Creation : SelectedMockUiModel

    @Immutable
    data class Edition(val existing: MockNetworkUiModel) : SelectedMockUiModel
}
