package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

/** The kind of a preference value, displayed as a badge in the table. */
@Immutable
enum class PreferenceValueTypeUiModel(val label: String) {
    Str("STR"),
    Int("INT"),
    Long("LONG"),
    Float("FLOAT"),
    Bool("BOOL"),
    Json("JSON"),
    Set("SET"),
}
