package io.github.openflocon.flocondesktop.features.network.badquality.edition.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SelectedBadQualityUiModel {

    val config: BadQualityConfigUiModel?

    @Immutable
    data object Creation : SelectedBadQualityUiModel {
        override val config: BadQualityConfigUiModel? = null
    }

    @Immutable
    data class Edition(override val config: BadQualityConfigUiModel) : SelectedBadQualityUiModel
}
