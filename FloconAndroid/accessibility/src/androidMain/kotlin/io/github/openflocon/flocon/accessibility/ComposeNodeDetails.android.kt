package io.github.openflocon.flocon.accessibility

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.state.ToggleableState

/**
 * What a screen reader reads on a Compose element, [label] being its text or content description.
 *
 * Compose is a `compileOnly` dependency of this module: this must only be called once [isComposeAvailable] is true.
 */
internal fun SemanticsNode.toNodeDetails(label: String?): NodeDetails {
    val config = config
    val role = config.getOrNull(SemanticsProperties.Role)?.toText()
    val state = config.getOrNull(SemanticsProperties.StateDescription)?.takeIf { it.isNotBlank() }
        ?: config.getOrNull(SemanticsProperties.ToggleableState)?.let {
            when (it) {
                ToggleableState.On -> checkedState(role, true)
                ToggleableState.Off -> checkedState(role, false)
                ToggleableState.Indeterminate -> "partially checked"
            }
        }
    val onClick = config.getOrNull(SemanticsActions.OnClick)

    return NodeDetails(
        label = label,
        role = role,
        state = state,
        clickable = onClick != null,
        clickLabel = onClick?.label?.takeIf { it.isNotBlank() },
        enabled = !config.contains(SemanticsProperties.Disabled),
        selected = config.getOrNull(SemanticsProperties.Selected) == true,
        heading = config.contains(SemanticsProperties.Heading),
        customActions = config.getOrNull(SemanticsActions.CustomActions)
            ?.map { it.label }
            ?.filter { it.isNotBlank() }
            .orEmpty(),
    )
}

private fun Role.toText(): String = when (this) {
    Role.Button -> "Button"
    Role.Checkbox -> "Check box"
    Role.RadioButton -> "Radio button"
    Role.Switch -> "Switch"
    Role.Tab -> "Tab"
    Role.Image -> "Image"
    Role.DropdownList -> "Drop-down list"
    else -> toString()
}
