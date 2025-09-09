package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class SharedPreferenceToEdit(
    val row: SharedPreferencesRowUiModel,
    val stringValue: SharedPreferencesRowUiModel.Value.StringValue,
)
