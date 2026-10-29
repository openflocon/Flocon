package io.github.openflocon.flocon.accessibility

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.accessibility.model.AccessibilityFocusedNodeItem
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** An element of the screen a screen reader can stop on, and how to give it the accessibility focus. */
internal class FocusTarget(
    val item: AccessibilityFocusedNodeItem,
    val focus: () -> Boolean,
    val activate: () -> Boolean,
)

private var resumedActivity: WeakReference<Activity>? = null

internal fun onActivityResumedForFocus(activity: Activity) {
    resumedActivity = WeakReference(activity)
}

internal actual suspend fun requestAccessibilityFocus(
    request: AccessibilityFocusRequest,
    currentBounds: String?,
): AccessibilityFocusedNodeItem = suspendCoroutine { continuation ->
    val run = {
        val result = try {
            resumedActivity?.get()?.let { focusOnMainThread(it, request, currentBounds) }
        } catch (t: Throwable) {
            FloconLogger.logError("error on requestAccessibilityFocus", t)
            null
        }
        continuation.resume(result ?: AccessibilityFocusedNodeItem())
    }
    if (Looper.myLooper() == Looper.getMainLooper()) run() else Handler(Looper.getMainLooper()).post(run)
}

private fun focusOnMainThread(
    activity: Activity,
    request: AccessibilityFocusRequest,
    currentBounds: String?,
): AccessibilityFocusedNodeItem? {
    val decorView = activity.window?.decorView ?: return null
    val targets = buildList { decorView.collectFocusTargets(activity.javaClass.name, this) }
    val currentIndex = currentBounds?.let { bounds -> targets.indexOfFirst { it.item.bounds == bounds } } ?: -1

    val index = targetFocusIndex(request, currentIndex, targets.size)
    if (index == null) {
        // nothing to move to: describe where we are
        return targets.getOrNull(currentIndex)?.item?.withPosition(currentIndex, targets.size)
    }
    val target = targets[index]
    when (request) {
        AccessibilityFocusRequest.Current -> Unit
        AccessibilityFocusRequest.Activate -> target.activate()
        AccessibilityFocusRequest.Next, AccessibilityFocusRequest.Previous -> target.focus()
    }
    return target.item.withPosition(index, targets.size)
}

private fun AccessibilityFocusedNodeItem.withPosition(index: Int, count: Int) = copy(
    index = index,
    count = count,
    hasNext = index < count - 1,
    hasPrevious = index > 0,
)

private fun View.collectFocusTargets(screen: String, out: MutableList<FocusTarget>) {
    if (visibility != View.VISIBLE) return
    if (isComposeAvailable && ComposeFocusOrder.collect(this, screen, out)) return
    if (importantForAccessibility == View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) return

    if (isFocusTarget()) {
        out += FocusTarget(
            item = toFocusedNodeItem(screen),
            focus = { performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null) },
            activate = { performAccessibilityAction(AccessibilityNodeInfo.ACTION_CLICK, null) },
        )
    }
    if (this is ViewGroup) {
        for (i in 0 until childCount) getChildAt(i).collectFocusTargets(screen, out)
    }
}

/** Leaf views, and the groups that are read as a whole, are what a screen reader stops on. */
private fun View.isFocusTarget(): Boolean {
    if (!isImportantForAccessibility) return false
    if (this !is ViewGroup) return true
    return !contentDescription.isNullOrBlank() || isClickable
}

private fun View.toFocusedNodeItem(screen: String): AccessibilityFocusedNodeItem {
    val onScreen = IntArray(2)
    getLocationOnScreen(onScreen)
    return AccessibilityFocusedNodeItem(
        found = true,
        text = (this as? TextView)?.text?.toString()?.takeIf { it.isNotBlank() },
        contentDescription = contentDescription?.toString()?.takeIf { it.isNotBlank() },
        className = javaClass.name,
        screen = screen,
        bounds = "[${onScreen[0]},${onScreen[1]}][${onScreen[0] + width},${onScreen[1] + height}]",
        extras = buildMap {
            put("source", "view")
            if (id != View.NO_ID) {
                try {
                    put("viewId", resources.getResourceEntryName(id))
                } catch (_: Exception) {
                    // generated ids have no name
                }
            }
        },
    )
}
