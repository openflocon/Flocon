package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SharedPreferencesRowsStateUiModel {
    val rows: List<SharedPreferencesRowUiModel>

    @Immutable
    data object Loading : SharedPreferencesRowsStateUiModel {
        override val rows = emptyList<SharedPreferencesRowUiModel>()
    }

    @Immutable
    data object Empty : SharedPreferencesRowsStateUiModel {
        override val rows = emptyList<SharedPreferencesRowUiModel>()
    }

    @Immutable
    data class WithContent(
        override val rows: List<SharedPreferencesRowUiModel>,
    ) : SharedPreferencesRowsStateUiModel
}

fun previewSharedPreferencesRowsStateUiModel(): SharedPreferencesRowsStateUiModel = SharedPreferencesRowsStateUiModel.WithContent(
    rows =
    listOf(
        previewSharedPreferencesStringRowUiModel(),
        previewSharedPreferencesStringRowUiModel(),
        previewSharedPreferencesStringRowUiModel(),
    ),
)
