package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

@Immutable
enum class PreferenceEditTab { Formatted, Raw }

@Immutable
enum class PreferenceEditStatus {
    Unchanged,
    Modified,
    ModifiedValidJson,
    InvalidJson,
}

/** State of the editor of a long / json value, displayed in the right panel. */
@Immutable
data class PreferenceEditUiModel(
    val key: String,
    val isJson: Boolean,
    // ex: "3 items · 412 B"
    val sizeLabel: String,
    val tab: PreferenceEditTab,
    val text: String,
    val lineCount: Int,
    val status: PreferenceEditStatus,
    val canSend: Boolean,
)

fun previewPreferenceEditUiModel(
    status: PreferenceEditStatus = PreferenceEditStatus.Unchanged,
) = PreferenceEditUiModel(
    key = "cached_users",
    isJson = true,
    sizeLabel = "3 items · 412 B",
    tab = PreferenceEditTab.Formatted,
    text = "[\n  {\n    \"id\": 1,\n    \"username\": \"user_528deafd\"\n  }\n]",
    lineCount = 6,
    status = status,
    canSend = status == PreferenceEditStatus.ModifiedValidJson,
)
