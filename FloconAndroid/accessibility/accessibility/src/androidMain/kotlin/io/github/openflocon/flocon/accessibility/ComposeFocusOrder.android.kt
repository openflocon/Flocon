package io.github.openflocon.flocon.accessibility

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import io.github.openflocon.flocon.accessibility.model.AccessibilityFocusedNodeItem

/**
 * Lists the Compose elements a screen reader stops on, in the order of the merged semantics tree.
 *
 * Compose is a `compileOnly` dependency of this module: this object must only be touched once
 * [isComposeAvailable] is true, so apps without Compose never load it.
 */
internal object ComposeFocusOrder {

    /** Adds the elements of [composeView] to [out], and returns false when it is not a Compose root. */
    fun collect(composeView: View, screen: String, out: MutableList<FocusTarget>): Boolean {
        val root = composeView as? RootForTest ?: return false

        val onScreen = IntArray(2)
        val inWindow = IntArray(2)
        composeView.getLocationOnScreen(onScreen)
        composeView.getLocationInWindow(inWindow)
        val offsetX = onScreen[0] - inWindow[0]
        val offsetY = onScreen[1] - inWindow[1]

        root.semanticsOwner.rootSemanticsNode.collect { node ->
            out += FocusTarget(
                item = node.toItem(screen, offsetX, offsetY),
                focus = {
                    // the virtual id of a Compose element is the id of its semantics node
                    composeView.accessibilityNodeProvider
                        ?.performAction(node.id, AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null) == true
                },
            )
        }
        return true
    }

    private fun SemanticsNode.collect(onFocusable: (SemanticsNode) -> Unit) {
        val config = config
        if (config.contains(SemanticsProperties.InvisibleToUser)) return
        if (label() != null || config.contains(SemanticsActions.OnClick)) onFocusable(this)
        // a merging node is read as a whole, with the text of its descendants
        if (config.isMergingSemanticsOfDescendants) return
        children.forEach { it.collect(onFocusable) }
    }

    private fun SemanticsNode.label(): String? {
        return text() ?: contentDescription()
    }

    private fun SemanticsNode.text() = config.getOrNull(SemanticsProperties.Text)
        ?.joinToString(" ") { it.text }
        ?.takeIf { it.isNotBlank() }

    private fun SemanticsNode.contentDescription() = config.getOrNull(SemanticsProperties.ContentDescription)
        ?.joinToString(" ")
        ?.takeIf { it.isNotBlank() }

    private fun SemanticsNode.toItem(screen: String, offsetX: Int, offsetY: Int): AccessibilityFocusedNodeItem {
        val config = config
        val bounds = boundsInWindow
        return AccessibilityFocusedNodeItem(
            found = true,
            text = text(),
            contentDescription = contentDescription(),
            className = config.getOrNull(SemanticsProperties.Role)?.toString() ?: "Compose",
            screen = screen,
            bounds = "[${(bounds.left + offsetX).toInt()},${(bounds.top + offsetY).toInt()}]" +
                "[${(bounds.right + offsetX).toInt()},${(bounds.bottom + offsetY).toInt()}]",
            extras = buildMap {
                put("source", "compose")
                config.getOrNull(SemanticsProperties.TestTag)?.let { put("testTag", it) }
                config.getOrNull(SemanticsProperties.StateDescription)?.let { put("stateDescription", it) }
            },
        )
    }
}
