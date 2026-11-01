package io.github.openflocon.flocon.accessibility

import android.annotation.TargetApi
import android.app.Activity
import android.view.ActionMode
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.SearchEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent as AndroidAccessibilityEvent
import android.widget.TextView
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent

internal const val CLICK_EVENT_TYPE = "CLICK"

/** Cached: the Compose hit-test is only class-loaded when Compose is on the classpath. */
internal val isComposeAvailable: Boolean by lazy {
    try {
        Class.forName("androidx.compose.ui.node.RootForTest")
        true
    } catch (t: Throwable) {
        false
    }
}

/**
 * Wraps the window callback of [activity] to observe taps, without any accessibility service.
 * Does nothing when the window has no callback or is already wrapped.
 */
internal fun installClickCapture(activity: Activity, onEvent: (AccessibilityEvent) -> Unit) {
    val window = activity.window ?: return
    val original = window.callback ?: return
    if (original is FloconWindowCallback) return

    val viewConfiguration = ViewConfiguration.get(activity)
    window.callback = FloconWindowCallback(
        original = original,
        window = window,
        screen = activity.javaClass.name,
        tapDetector = TapDetector(
            touchSlop = viewConfiguration.scaledTouchSlop.toFloat(),
            maxDurationMs = ViewConfiguration.getLongPressTimeout().toLong(),
        ),
        onEvent = onEvent,
    )
}

/**
 * Forwards every call to the original callback, unchanged. It only observes the touch events:
 * it never consumes or alters them.
 */
private class FloconWindowCallback(
    private val original: Window.Callback,
    private val window: Window,
    private val screen: String,
    private val tapDetector: TapDetector,
    private val onEvent: (AccessibilityEvent) -> Unit,
) : Window.Callback {

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // resolved before the app handles the touch, the click may change or remove the view
        val click = try {
            event?.let { observe(it) }
        } catch (t: Throwable) {
            FloconLogger.logError("error on capturing a click", t)
            null
        }
        val handled = original.dispatchTouchEvent(event)
        if (click != null) {
            try {
                onEvent(click)
            } catch (t: Throwable) {
                FloconLogger.logError("error on sending a click", t)
            }
        }
        return handled
    }

    /** @return the click to report when [event] ends a tap */
    private fun observe(event: MotionEvent): AccessibilityEvent? {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> tapDetector.onDown(event.rawX, event.rawY, event.eventTime)
            MotionEvent.ACTION_MOVE -> tapDetector.onMove(event.rawX, event.rawY)
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_CANCEL -> tapDetector.cancel()
            MotionEvent.ACTION_UP -> {
                if (tapDetector.onUp(event.rawX, event.rawY, event.eventTime)) {
                    return findClickEvent(window.decorView, event.rawX, event.rawY, screen)
                }
            }
        }
        return null
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean = original.dispatchKeyEvent(event)

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean = original.dispatchKeyShortcutEvent(event)

    override fun dispatchTrackballEvent(event: MotionEvent?): Boolean = original.dispatchTrackballEvent(event)

    override fun dispatchGenericMotionEvent(event: MotionEvent?): Boolean = original.dispatchGenericMotionEvent(event)

    override fun dispatchPopulateAccessibilityEvent(event: AndroidAccessibilityEvent?): Boolean =
        original.dispatchPopulateAccessibilityEvent(event)

    override fun onCreatePanelView(featureId: Int): View? = original.onCreatePanelView(featureId)

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean = original.onCreatePanelMenu(featureId, menu)

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu): Boolean =
        original.onPreparePanel(featureId, view, menu)

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean = original.onMenuOpened(featureId, menu)

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean =
        original.onMenuItemSelected(featureId, item)

    override fun onWindowAttributesChanged(attrs: WindowManager.LayoutParams?) =
        original.onWindowAttributesChanged(attrs)

    override fun onContentChanged() = original.onContentChanged()

    override fun onWindowFocusChanged(hasFocus: Boolean) = original.onWindowFocusChanged(hasFocus)

    override fun onAttachedToWindow() = original.onAttachedToWindow()

    override fun onDetachedFromWindow() = original.onDetachedFromWindow()

    override fun onPanelClosed(featureId: Int, menu: Menu) = original.onPanelClosed(featureId, menu)

    @Deprecated("Deprecated in Java")
    override fun onSearchRequested(): Boolean = original.onSearchRequested()

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean = original.onSearchRequested(searchEvent)

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? =
        original.onWindowStartingActionMode(callback)

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? =
        original.onWindowStartingActionMode(callback, type)

    override fun onActionModeStarted(mode: ActionMode?) = original.onActionModeStarted(mode)

    override fun onActionModeFinished(mode: ActionMode?) = original.onActionModeFinished(mode)

    @TargetApi(24)
    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>?,
        menu: Menu?,
        deviceId: Int,
    ) = original.onProvideKeyboardShortcuts(data, menu, deviceId)

    @TargetApi(26)
    override fun onPointerCaptureChanged(hasCapture: Boolean) = original.onPointerCaptureChanged(hasCapture)
}

/** The clickable element under the point, as a screen reader would describe it. */
private fun findClickEvent(decorView: View, screenX: Float, screenY: Float, screen: String): AccessibilityEvent? {
    val location = IntArray(2)
    val inWindow = IntArray(2)
    decorView.getLocationOnScreen(location)
    decorView.getLocationInWindow(inWindow)
    val point = Point(
        screenX = screenX.toInt(),
        screenY = screenY.toInt(),
        windowX = screenX.toInt() - (location[0] - inWindow[0]),
        windowY = screenY.toInt() - (location[1] - inWindow[1]),
    )
    return findClickEvent(decorView, point, screen)
}

private class Point(val screenX: Int, val screenY: Int, val windowX: Int, val windowY: Int)

private fun findClickEvent(view: View, point: Point, screen: String): AccessibilityEvent? {
    if (!view.isShown) return null
    val visible = android.graphics.Rect()
    if (!view.getGlobalVisibleRect(visible) || !visible.contains(point.windowX, point.windowY)) return null

    if (view is ViewGroup) {
        // last children are drawn on top
        for (i in view.childCount - 1 downTo 0) {
            findClickEvent(view.getChildAt(i), point, screen)?.let { return it }
        }
    }
    if (isComposeAvailable) {
        ComposeHitTest.find(view, point.screenX.toFloat(), point.screenY.toFloat(), screen)?.let { return it }
    }
    if (view.isClickable || view.isLongClickable) return view.toClickEvent(screen)
    return null
}

private fun View.toClickEvent(screen: String): AccessibilityEvent {
    val text = ((this as? TextView)?.text?.toString() ?: descendantsText())?.takeIf { it.isNotBlank() }
    val contentDescription = contentDescription?.toString()?.takeIf { it.isNotBlank() }

    val location = IntArray(2)
    getLocationOnScreen(location)
    val bounds = "[${location[0]},${location[1]}][${location[0] + width},${location[1] + height}]"

    return AccessibilityEvent(
        type = CLICK_EVENT_TYPE,
        text = text,
        contentDescription = contentDescription,
        className = javaClass.name,
        screen = screen,
        bounds = bounds,
        extras = buildMap {
            put("source", "view")
            resourceEntryName()?.let { put("viewId", it) }
            put("enabled", isEnabled.toString())
            if (text == null && contentDescription == null) put("missingLabel", "true")
        },
    )
}

/** What a screen reader reads for a clickable container: the text of the visible views inside. */
private fun View.descendantsText(): String? {
    val group = this as? ViewGroup ?: return null
    val texts = mutableListOf<String>()
    fun collect(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.visibility != View.VISIBLE) continue
            if (child is TextView) {
                child.text?.toString()?.takeIf { it.isNotBlank() }?.let { texts += it }
            } else if (child is ViewGroup) {
                collect(child)
            }
        }
    }
    collect(group)
    return texts.joinToString(" ").takeIf { it.isNotBlank() }
}

private fun View.resourceEntryName(): String? {
    if (id == View.NO_ID) return null
    return try {
        resources.getResourceEntryName(id)
    } catch (t: Throwable) {
        null
    }
}
