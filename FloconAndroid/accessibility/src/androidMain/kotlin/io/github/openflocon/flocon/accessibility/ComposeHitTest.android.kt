package io.github.openflocon.flocon.accessibility

import android.view.View
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent

/**
 * Finds the clickable Compose element under a point, as an accessibility service would describe it.
 *
 * Compose is a `compileOnly` dependency of this module: this object must only be touched once
 * [isComposeAvailable] is true, so apps without Compose never load it.
 */
internal object ComposeHitTest {

    /**
     * @param composeView a view that may be a Compose root (`AndroidComposeView`)
     * @param screenX x of the touch, in screen coordinates
     * @param screenY y of the touch, in screen coordinates
     * @return the click event, or null when [composeView] is not a Compose root or nothing clickable is there
     */
    fun find(composeView: View, screenX: Float, screenY: Float, screen: String): AccessibilityEvent? {
        val root = composeView as? RootForTest ?: return null

        val onScreen = IntArray(2)
        val inWindow = IntArray(2)
        composeView.getLocationOnScreen(onScreen)
        composeView.getLocationInWindow(inWindow)
        // boundsInWindow is relative to the window, the touch is relative to the screen
        val offsetX = onScreen[0] - inWindow[0]
        val offsetY = onScreen[1] - inWindow[1]

        // the merged tree is what TalkBack reads: a clickable node carries the text of its children
        val node = root.semanticsOwner.rootSemanticsNode.deepestClickable(
            x = screenX - offsetX,
            y = screenY - offsetY,
        ) ?: return null

        return node.toEvent(screen, offsetX, offsetY)
    }

    private fun SemanticsNode.deepestClickable(x: Float, y: Float): SemanticsNode? {
        val bounds = boundsInWindow
        if (x < bounds.left || x >= bounds.right || y < bounds.top || y >= bounds.bottom) return null

        // later children are drawn on top
        val children = children
        for (i in children.indices.reversed()) {
            children[i].deepestClickable(x, y)?.let { return it }
        }
        return if (config.contains(SemanticsActions.OnClick)) this else null
    }

    private fun SemanticsNode.toEvent(screen: String, offsetX: Int, offsetY: Int): AccessibilityEvent {
        val config = config
        val text = config.getOrNull(SemanticsProperties.Text)
            ?.joinToString(" ") { it.text }
            ?.takeIf { it.isNotBlank() }
        val contentDescription = config.getOrNull(SemanticsProperties.ContentDescription)
            ?.joinToString(" ")
            ?.takeIf { it.isNotBlank() }
        val role = config.getOrNull(SemanticsProperties.Role)?.toString()
        val testTag = config.getOrNull(SemanticsProperties.TestTag)
        val stateDescription = config.getOrNull(SemanticsProperties.StateDescription)
        val clickLabel = config.getOrNull(SemanticsActions.OnClick)?.label
        val enabled = !config.contains(SemanticsProperties.Disabled)

        val bounds = boundsInWindow
        val boundsOnScreen = "[${(bounds.left + offsetX).toInt()},${(bounds.top + offsetY).toInt()}]" +
            "[${(bounds.right + offsetX).toInt()},${(bounds.bottom + offsetY).toInt()}]"

        return AccessibilityEvent(
            type = CLICK_EVENT_TYPE,
            text = text,
            contentDescription = contentDescription,
            className = role ?: "Compose",
            screen = screen,
            bounds = boundsOnScreen,
            extras = buildMap {
                put("source", "compose")
                testTag?.let { put("testTag", it) }
                stateDescription?.let { put("stateDescription", it) }
                clickLabel?.let { put("clickLabel", it) }
                put("enabled", enabled.toString())
                if (text == null && contentDescription == null) put("missingLabel", "true")
            },
        )
    }
}
