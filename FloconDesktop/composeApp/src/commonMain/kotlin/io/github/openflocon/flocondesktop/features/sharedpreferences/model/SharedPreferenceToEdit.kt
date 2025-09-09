package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

@Immutable
data class SharedPreferenceToEdit(
    val row: SharedPreferencesRowUiModel,
    val stringValue: SharedPreferencesRowUiModel.Value.StringValue,
)
