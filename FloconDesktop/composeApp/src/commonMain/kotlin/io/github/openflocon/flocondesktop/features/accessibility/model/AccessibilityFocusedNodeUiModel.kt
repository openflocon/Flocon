package io.github.openflocon.flocondesktop.features.accessibility.model

import androidx.compose.runtime.Immutable

/**
 * The element the screen reader is on
 */
@Immutable
data class AccessibilityFocusedNodeUiModel(
    val title: String,
    val className: String?,
    val bounds: String?,
    val screen: String?,
    /** 1-based position in the focus order of the screen, with the number of elements, when known */
    val position: Position?,
) {
    @Immutable
    data class Position(
        val number: Int,
        val count: Int,
    )
}

fun previewAccessibilityFocusedNodeUiModel() = AccessibilityFocusedNodeUiModel(
    title = "Add to cart",
    className = "Button",
    bounds = "[48,1530][1032,1674]",
    screen = "CartActivity",
    position = AccessibilityFocusedNodeUiModel.Position(number = 3, count = 12),
)
