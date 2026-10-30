package io.github.openflocon.flocondesktop.features.accessibility.mapper

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_type_announcement
import flocondesktop.composeapp.generated.resources.accessibility_type_click
import flocondesktop.composeapp.generated.resources.accessibility_type_element_focused
import flocondesktop.composeapp.generated.resources.accessibility_type_view_accessibility_focus_cleared
import flocondesktop.composeapp.generated.resources.accessibility_type_view_accessibility_focused
import flocondesktop.composeapp.generated.resources.accessibility_type_view_clicked
import flocondesktop.composeapp.generated.resources.accessibility_type_view_focused
import flocondesktop.composeapp.generated.resources.accessibility_type_view_long_clicked
import flocondesktop.composeapp.generated.resources.accessibility_type_view_scrolled
import flocondesktop.composeapp.generated.resources.accessibility_type_view_selected
import flocondesktop.composeapp.generated.resources.accessibility_type_view_text_changed
import flocondesktop.composeapp.generated.resources.accessibility_type_window_content_changed
import flocondesktop.composeapp.generated.resources.accessibility_type_window_state_changed
import org.jetbrains.compose.resources.StringResource

/** The readable name of an event type, null for the types without one: see [fallbackTypeLabel]. */
internal fun accessibilityTypeLabelRes(type: String): StringResource? = when (type) {
    "ANNOUNCEMENT" -> Res.string.accessibility_type_announcement
    "CLICK" -> Res.string.accessibility_type_click
    "ELEMENT_FOCUSED" -> Res.string.accessibility_type_element_focused
    "VIEW_ACCESSIBILITY_FOCUS_CLEARED" -> Res.string.accessibility_type_view_accessibility_focus_cleared
    "VIEW_ACCESSIBILITY_FOCUSED" -> Res.string.accessibility_type_view_accessibility_focused
    "VIEW_CLICKED" -> Res.string.accessibility_type_view_clicked
    "VIEW_FOCUSED" -> Res.string.accessibility_type_view_focused
    "VIEW_LONG_CLICKED" -> Res.string.accessibility_type_view_long_clicked
    "VIEW_SCROLLED" -> Res.string.accessibility_type_view_scrolled
    "VIEW_SELECTED" -> Res.string.accessibility_type_view_selected
    "VIEW_TEXT_CHANGED" -> Res.string.accessibility_type_view_text_changed
    "WINDOW_CONTENT_CHANGED" -> Res.string.accessibility_type_window_content_changed
    "WINDOW_STATE_CHANGED" -> Res.string.accessibility_type_window_state_changed
    else -> null
}

/** `VIEW_HOVER_ENTER` -> `View hover enter`, for the types we have no name for (the type is a free string). */
internal fun fallbackTypeLabel(type: String): String = type.lowercase().replace('_', ' ').trim().replaceFirstChar { it.uppercase() }.ifEmpty { type }
