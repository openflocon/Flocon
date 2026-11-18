package io.github.openflocon.flocondesktop.features.accessibility.model

import androidx.compose.runtime.Immutable

@Immutable
data class AccessibilityRowUiModel(
    val id: String,
    val timeFormatted: String,
    val type: String,
    val text: String, // the text, else the content description, may be empty
    val className: String?,
    val missingLabel: Boolean, // clickable but a screen reader can't name it
)

fun previewAccessibilityRowUiModel(id: String = "1") = AccessibilityRowUiModel(
    id = id,
    timeFormatted = "12:03:45.120",
    type = "VIEW_FOCUSED",
    text = "Add to cart",
    className = "android.widget.Button",
    missingLabel = false,
)
