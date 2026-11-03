package io.github.openflocon.flocon.accessibility

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent as AndroidAccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem
import java.util.Collections
import java.util.WeakHashMap

internal actual fun startAccessibilityCapture(
    context: FloconContext,
    onEvent: (AccessibilityEvent) -> Unit,
    onState: (AccessibilityStateItem) -> Unit,
) {
    val application = context.context.applicationContext as? Application ?: return
    onApplicationForTalkBackSwitch(application)

    val accessibilityManager = application.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    if (accessibilityManager != null) {
        val sendState = { onState(accessibilityManager.toState()) }
        try {
            accessibilityManager.addAccessibilityStateChangeListener { sendState() }
            accessibilityManager.addTouchExplorationStateChangeListener { sendState() }
            sendState()
        } catch (t: Throwable) {
            FloconLogger.logError("error on accessibility state listeners", t)
        }
    }

    application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        private val hookedDecorViews: MutableSet<View> =
            Collections.newSetFromMap(WeakHashMap<View, Boolean>())

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = hook(activity)
        override fun onActivityResumed(activity: Activity) {
            onActivityResumedForFocus(activity)
            hook(activity)
        }
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit

        private fun hook(activity: Activity) {
            try {
                val decorView = activity.window?.decorView ?: return
                if (!hookedDecorViews.add(decorView)) return
                // clicks are captured from the touches, so they work without an accessibility service
                installClickCapture(activity, onEvent)
                decorView.setAccessibilityDelegate(FloconAccessibilityDelegate(
                    previous = decorView.currentAccessibilityDelegate(),
                    screen = activity.javaClass.name,
                    onEvent = onEvent,
                ))
            } catch (t: Throwable) {
                FloconLogger.logError("error on hooking accessibility delegate", t)
            }
        }
    })
}

private fun AccessibilityManager.toState() = AccessibilityStateItem(
    supported = true,
    enabled = isEnabled,
    touchExploration = isTouchExplorationEnabled,
    services = getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .map { it.id },
    canToggleTalkBack = canToggleTalkBack(),
)

/** The getter is public from API 29, before that it is only reachable by reflection. */
private fun View.currentAccessibilityDelegate(): View.AccessibilityDelegate? {
    return try {
        if (Build.VERSION.SDK_INT >= 29) {
            accessibilityDelegate
        } else {
            View::class.java.getDeclaredMethod("getAccessibilityDelegate")
                .apply { isAccessible = true }
                .invoke(this) as? View.AccessibilityDelegate
        }
    } catch (t: Throwable) {
        null
    }
}

/**
 * Forwards every call to the delegate that was already set (or to the default behavior),
 * and reports the events going up the view tree.
 */
private class FloconAccessibilityDelegate(
    private val previous: View.AccessibilityDelegate?,
    private val screen: String,
    private val onEvent: (AccessibilityEvent) -> Unit,
) : View.AccessibilityDelegate() {

    /** The last element that got the accessibility focus, to describe the "focus cleared" events. */
    private var lastFocused: AccessibilityEvent? = null

    override fun sendAccessibilityEvent(host: View, eventType: Int) {
        previous?.sendAccessibilityEvent(host, eventType) ?: super.sendAccessibilityEvent(host, eventType)
    }

    override fun performAccessibilityAction(host: View, action: Int, args: Bundle?): Boolean {
        return previous?.performAccessibilityAction(host, action, args)
            ?: super.performAccessibilityAction(host, action, args)
    }

    override fun sendAccessibilityEventUnchecked(host: View, event: AndroidAccessibilityEvent) {
        previous?.sendAccessibilityEventUnchecked(host, event) ?: super.sendAccessibilityEventUnchecked(host, event)
    }

    override fun dispatchPopulateAccessibilityEvent(host: View, event: AndroidAccessibilityEvent): Boolean {
        return previous?.dispatchPopulateAccessibilityEvent(host, event)
            ?: super.dispatchPopulateAccessibilityEvent(host, event)
    }

    override fun onPopulateAccessibilityEvent(host: View, event: AndroidAccessibilityEvent) {
        previous?.onPopulateAccessibilityEvent(host, event) ?: super.onPopulateAccessibilityEvent(host, event)
    }

    override fun onInitializeAccessibilityEvent(host: View, event: AndroidAccessibilityEvent) {
        previous?.onInitializeAccessibilityEvent(host, event) ?: super.onInitializeAccessibilityEvent(host, event)
    }

    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
        previous?.onInitializeAccessibilityNodeInfo(host, info) ?: super.onInitializeAccessibilityNodeInfo(host, info)
    }

    override fun addExtraDataToAccessibilityNodeInfo(
        host: View,
        info: AccessibilityNodeInfo,
        extraDataKey: String,
        arguments: Bundle?,
    ) {
        previous?.addExtraDataToAccessibilityNodeInfo(host, info, extraDataKey, arguments)
            ?: super.addExtraDataToAccessibilityNodeInfo(host, info, extraDataKey, arguments)
    }

    override fun onRequestSendAccessibilityEvent(
        host: ViewGroup,
        child: View,
        event: AndroidAccessibilityEvent,
    ): Boolean {
        try {
            val converted = event.toFloconEvent(screen)
            onEvent(
                when (event.eventType) {
                    // the focus is already on the element when the event is sent, but the event cannot name it yet
                    AndroidAccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED ->
                        converted.describedByFocusedElement(host.rootView, screen).also { lastFocused = it }

                    // the element has already lost the focus: it is the last one that had it
                    AndroidAccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED ->
                        converted.describingLostFocus(lastFocused)

                    else -> converted
                },
            )
        } catch (t: Throwable) {
            FloconLogger.logError("error on mapping accessibility event", t)
        }
        return previous?.onRequestSendAccessibilityEvent(host, child, event)
            ?: super.onRequestSendAccessibilityEvent(host, child, event)
    }

    override fun getAccessibilityNodeProvider(host: View): AccessibilityNodeProvider? {
        return previous?.getAccessibilityNodeProvider(host) ?: super.getAccessibilityNodeProvider(host)
    }
}

private fun AndroidAccessibilityEvent.toFloconEvent(screen: String): AccessibilityEvent {
    // many events (the accessibility focus ones, Compose) carry no text: the label is on the element they come from
    val source = readSource(
        eventText = text?.takeIf { it.isNotEmpty() }?.joinToString(" ")?.takeIf { it.isNotBlank() },
        eventContentDescription = contentDescription?.toString()?.takeIf { it.isNotBlank() },
    )
    return AccessibilityEvent(
        type = AndroidAccessibilityEvent.eventTypeToString(eventType).removePrefix("TYPE_"),
        text = source?.text ?: text?.takeIf { it.isNotEmpty() }?.joinToString(" "),
        contentDescription = source?.contentDescription ?: contentDescription?.toString(),
        className = className?.toString(),
        screen = screen,
        bounds = source?.bounds,
        extras = buildMap {
            packageName?.let { put("packageName", it.toString()) }
            source?.let { putAll(it.extras) }
        },
    )
}

/**
 * The accessibility events of an element carry no text (and Compose ones no source): the label, the bounds and the
 * details are the ones of the element that has the accessibility focus, as the remote shows them.
 */
private fun AccessibilityEvent.describedByFocusedElement(root: View, screen: String): AccessibilityEvent {
    val item = try {
        findAccessibilityFocusedItem(root, screen)
    } catch (t: Throwable) {
        FloconLogger.logError("error on describing the focused element", t)
        null
    } ?: return this

    return copy(
        text = text ?: item.text,
        contentDescription = contentDescription ?: item.contentDescription,
        className = item.className ?: className,
        bounds = item.bounds ?: bounds,
        extras = extras + item.extras,
    )
}

/** What is known of the element an event comes from. */
private class EventSource(
    val text: String?,
    val contentDescription: String?,
    val bounds: String,
    val extras: Map<String, String>,
)

/**
 * The element the event comes from: the text and content description of the event, else the ones of the element,
 * its bounds and what a screen reader knows about it. Null for the events without a source (announcements...).
 */
@Suppress("DEPRECATION")
private fun AndroidAccessibilityEvent.readSource(eventText: String?, eventContentDescription: String?): EventSource? {
    return try {
        val node = source ?: return null
        try {
            val text = eventText ?: node.text?.toString()?.takeIf { it.isNotBlank() }
            val contentDescription = eventContentDescription ?: node.contentDescription?.toString()?.takeIf { it.isNotBlank() }
            val rect = Rect()
            node.getBoundsInScreen(rect)
            EventSource(
                text = text,
                contentDescription = contentDescription,
                bounds = "[${rect.left},${rect.top}][${rect.right},${rect.bottom}]",
                extras = node.toNodeDetails(label = text ?: contentDescription).toExtras(),
            )
        } finally {
            // the node is a copy owned by the caller, recycling is a no-op since API 33
            node.recycle()
        }
    } catch (t: Throwable) {
        null
    }
}
