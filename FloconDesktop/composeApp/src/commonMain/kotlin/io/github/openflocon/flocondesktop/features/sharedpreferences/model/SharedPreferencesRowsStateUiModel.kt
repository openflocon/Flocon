package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed interface SharedPreferencesRowsStateUiModel {
    val rows: ImmutableList<SharedPreferencesRowUiModel>

    @Immutable
    data object Loading : SharedPreferencesRowsStateUiModel {
        override val rows = persistentListOf<SharedPreferencesRowUiModel>()
    }

    @Immutable
    data object Empty : SharedPreferencesRowsStateUiModel {
        override val rows = persistentListOf<SharedPreferencesRowUiModel>()
    }

    @Immutable
    data class WithContent(
        override val rows: ImmutableList<SharedPreferencesRowUiModel>,
    ) : SharedPreferencesRowsStateUiModel
}

fun previewSharedPreferencesRowsStateUiModel(): SharedPreferencesRowsStateUiModel = SharedPreferencesRowsStateUiModel.WithContent(
    rows =
    persistentListOf(
        previewSharedPreferencesStringRowUiModel(),
        previewSharedPreferencesStringRowUiModel(),
        previewSharedPreferencesStringRowUiModel(),
    ),
)
