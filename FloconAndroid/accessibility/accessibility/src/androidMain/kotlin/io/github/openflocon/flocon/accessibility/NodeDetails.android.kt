package io.github.openflocon.flocon.accessibility

import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo

/** Where Compose and the framework put the role of an element when it is not a standard widget. */
private const val ROLE_DESCRIPTION_KEY = "AccessibilityNodeInfo.roleDescription"

/** The ids of the actions of the framework stay below, the ones of an app (resource ids) are above. */
private const val CUSTOM_ACTION_ID_START = 0x02000000

/** What a screen reader reads on this node, [label] being the text or the content description of the element. */
@Suppress("DEPRECATION")
internal fun AccessibilityNodeInfo.toNodeDetails(label: String?): NodeDetails {
    val role = extras?.getCharSequence(ROLE_DESCRIPTION_KEY)?.toString()?.takeIf { it.isNotBlank() }
        ?: roleFromClassName(className?.toString())
    val stateDescription = if (Build.VERSION.SDK_INT >= 30) stateDescription?.toString()?.takeIf { it.isNotBlank() } else null

    return NodeDetails(
        label = label,
        role = role,
        state = stateDescription ?: if (isCheckable) checkedState(role, isChecked) else null,
        hint = if (Build.VERSION.SDK_INT >= 26) hintText?.toString()?.takeIf { it.isNotBlank() } else null,
        clickable = isClickable,
        enabled = isEnabled,
        selected = isSelected,
        heading = Build.VERSION.SDK_INT >= 28 && isHeading,
        customActions = actionList
            .filter { it.id >= CUSTOM_ACTION_ID_START }
            .mapNotNull { it.label?.toString()?.takeIf { label -> label.isNotBlank() } },
    )
}

/** What a screen reader reads on this view, without the text of its children: [label] already has it. */
internal fun View.toNodeDetails(label: String?): NodeDetails = try {
    createAccessibilityNodeInfo().toNodeDetails(label)
} catch (t: Throwable) {
    NodeDetails(label = label, enabled = isEnabled, clickable = isClickable)
}
