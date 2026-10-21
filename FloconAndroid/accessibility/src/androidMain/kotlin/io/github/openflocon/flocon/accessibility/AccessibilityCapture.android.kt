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
        override fun onActivityResumed(activity: Activity) = hook(activity)
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit

        private fun hook(activity: Activity) {
            try {
                val decorView = activity.window?.decorView ?: return
                if (!hookedDecorViews.add(decorView)) return
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
            onEvent(event.toFloconEvent(screen))
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
    return AccessibilityEvent(
        type = AndroidAccessibilityEvent.eventTypeToString(eventType).removePrefix("TYPE_"),
        text = text?.takeIf { it.isNotEmpty() }?.joinToString(" "),
        contentDescription = contentDescription?.toString(),
        className = className?.toString(),
        screen = screen,
        bounds = boundsOfSource(),
        extras = buildMap {
            packageName?.let { put("packageName", it.toString()) }
        },
    )
}

@Suppress("DEPRECATION")
private fun AndroidAccessibilityEvent.boundsOfSource(): String? {
    return try {
        val node = source ?: return null
        val rect = Rect()
        try {
            node.getBoundsInScreen(rect)
        } finally {
            // the node is a copy owned by the caller, recycling is a no-op since API 33
            node.recycle()
        }
        "[${rect.left},${rect.top}][${rect.right},${rect.bottom}]"
    } catch (t: Throwable) {
        null
    }
}
