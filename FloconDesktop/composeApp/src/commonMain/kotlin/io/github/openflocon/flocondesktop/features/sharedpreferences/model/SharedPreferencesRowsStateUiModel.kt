package io.github.openflocon.flocondesktop.features.sharedpreferences.model

sealed interface SharedPreferencesRowsStateUiModel {
    val rows: List<SharedPreferencesRowUiModel>

    data object Loading : SharedPreferencesRowsStateUiModel {
        override val rows = emptyList<SharedPreferencesRowUiModel>()
    }

    data object Empty : SharedPreferencesRowsStateUiModel {
        override val rows = emptyList<SharedPreferencesRowUiModel>()
    }

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
