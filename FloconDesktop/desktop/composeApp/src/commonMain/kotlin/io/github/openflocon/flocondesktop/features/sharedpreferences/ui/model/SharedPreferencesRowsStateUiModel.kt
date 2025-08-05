package io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model

sealed interface SharedPreferencesRowsStateUiModel {
    data object Loading : SharedPreferencesRowsStateUiModel

    data object Empty : SharedPreferencesRowsStateUiModel

    data class WithContent(
        val rows: List<SharedPreferencesRowUiModel>,
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
