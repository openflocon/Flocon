package io.github.openflocon.flocondesktop.features.accessibility.model

import androidx.compose.runtime.Immutable

@Immutable
data class AccessibilityDetailUiModel(
    val id: String,
    val title: String,
    val type: String,
    val timeFormatted: String,
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val screen: String?,
    val bounds: String?,
    val extras: List<Pair<String, String>>,
)
