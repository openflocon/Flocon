package io.github.openflocon.flocon.accessibility

/**
 * What a screen reader knows about an element, to describe it in the `extras` of an event.
 *
 * @param label the text or content description of the element
 * @param role how the kind of element is read, e.g. `Button`, `Check box`
 * @param state how its state is read, e.g. `checked`, `on`, or the state description of the app
 * @param clickLabel what the click does, when the app says it
 */
internal data class NodeDetails(
    val label: String? = null,
    val role: String? = null,
    val state: String? = null,
    val hint: String? = null,
    val clickable: Boolean = false,
    val clickLabel: String? = null,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val heading: Boolean = false,
    val customActions: List<String> = emptyList(),
)

/** The keys of [NodeDetails] for the `extras` of an event, only the ones that tell something. */
internal fun NodeDetails.toExtras(): Map<String, String> = buildMap {
    put("enabled", enabled.toString())
    role?.let { put("role", it) }
    state?.let { put("stateDescription", it) }
    hint?.let { put("hint", it) }
    clickLabel?.let { put("clickLabel", it) }
    if (clickable) put("clickable", "true")
    if (selected) put("selected", "true")
    if (heading) put("heading", "true")
    if (customActions.isNotEmpty()) put("customActions", customActions.joinToString(" | "))
    spokenText()?.let { put("spoken", it) }
}

/**
 * What a screen reader would likely say for the element: label, role, state, hint, then what a double tap does.
 * It is a reconstruction, the app cannot hear the screen reader: the settings of the user change what it really says.
 */
internal fun NodeDetails.spokenText(): String? = listOfNotNull(
    label,
    role,
    state,
    hint,
    "disabled".takeIf { !enabled },
    "heading".takeIf { heading },
    "selected".takeIf { selected },
    "double tap to ${clickLabel ?: "activate"}".takeIf { clickable && enabled },
).map { it.trim() }.filter { it.isNotEmpty() }.joinToString(", ").takeIf { it.isNotEmpty() }

/** The way a screen reader reads a Android widget class, null for the classes it does not announce. */
internal fun roleFromClassName(className: String?): String? = when (className?.substringAfterLast('.')) {
    "Button", "ImageButton", "MaterialButton", "AppCompatButton" -> "Button"
    "CheckBox", "AppCompatCheckBox", "MaterialCheckBox" -> "Check box"
    "RadioButton", "AppCompatRadioButton", "MaterialRadioButton" -> "Radio button"
    "Switch", "SwitchCompat", "SwitchMaterial", "MaterialSwitch" -> "Switch"
    "ToggleButton" -> "Toggle button"
    "EditText", "AppCompatEditText", "TextInputEditText" -> "Edit box"
    "ImageView", "AppCompatImageView" -> "Image"
    "SeekBar", "AppCompatSeekBar" -> "Seek control"
    "Spinner", "AppCompatSpinner" -> "Drop-down list"
    "TabWidget" -> "Tab"
    else -> null
}

/** The state of a checkable element as a screen reader says it: on / off for a switch, checked / not checked otherwise. */
internal fun checkedState(role: String?, checked: Boolean): String = when (role) {
    "Switch", "Toggle button" -> if (checked) "on" else "off"
    else -> if (checked) "checked" else "not checked"
}
