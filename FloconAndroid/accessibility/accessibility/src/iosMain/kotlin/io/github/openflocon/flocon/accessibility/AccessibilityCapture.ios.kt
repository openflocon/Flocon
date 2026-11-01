@file:OptIn(ExperimentalForeignApi::class)

package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNumber
import platform.Foundation.NSOperationQueue
import platform.Foundation.valueForKey
import platform.darwin.NSObject
import platform.UIKit.UIAccessibilityAnnouncementDidFinishNotification
import platform.UIKit.UIAccessibilityAnnouncementKeyStringValue
import platform.UIKit.UIAccessibilityAnnouncementKeyWasSuccessful
import platform.UIKit.UIAccessibilityAssistiveTechnologyKey
import platform.UIKit.UIAccessibilityElementFocusedNotification
import platform.UIKit.UIAccessibilityFocusedElementKey
import platform.UIKit.UIAccessibilityIsSwitchControlRunning
import platform.UIKit.UIAccessibilityIsVoiceOverRunning
import platform.UIKit.UIAccessibilitySwitchControlStatusDidChangeNotification
import platform.UIKit.UIAccessibilityVoiceOverStatusDidChangeNotification

// the observers must stay referenced for the whole life of the app
private val observers = mutableListOf<Any>()

internal actual fun startAccessibilityCapture(
    context: FloconContext,
    onEvent: (AccessibilityEvent) -> Unit,
    onState: (AccessibilityStateItem) -> Unit,
) {
    val center = NSNotificationCenter.defaultCenter
    val queue = NSOperationQueue.mainQueue

    fun observe(name: String?, block: (NSNotification) -> Unit) {
        observers += center.addObserverForName(name = name, `object` = null, queue = queue) { notification ->
            try {
                notification?.let(block)
            } catch (t: Throwable) {
                FloconLogger.logError("error on accessibility notification", t)
            }
        }
    }

    observe(UIAccessibilityElementFocusedNotification) { notification ->
        onEvent(notification.toFocusedEvent())
    }
    observe(UIAccessibilityAnnouncementDidFinishNotification) { notification ->
        onEvent(notification.toAnnouncementEvent())
    }
    observe(UIAccessibilityVoiceOverStatusDidChangeNotification) { onState(currentState()) }
    observe(UIAccessibilitySwitchControlStatusDidChangeNotification) { onState(currentState()) }

    onState(currentState())
}

private fun currentState(): AccessibilityStateItem {
    val voiceOver = UIAccessibilityIsVoiceOverRunning()
    val switchControl = UIAccessibilityIsSwitchControlRunning()
    return AccessibilityStateItem(
        supported = true,
        enabled = voiceOver || switchControl,
        touchExploration = voiceOver,
        services = listOfNotNull(
            "VoiceOver".takeIf { voiceOver },
            "SwitchControl".takeIf { switchControl },
        ),
    )
}

private fun NSNotification.toFocusedEvent(): AccessibilityEvent {
    val element = userInfo?.get(UIAccessibilityFocusedElementKey) as? NSObject
    return AccessibilityEvent(
        type = "ELEMENT_FOCUSED",
        text = element?.stringProperty("accessibilityValue"),
        contentDescription = element?.stringProperty("accessibilityLabel"),
        className = element?.let { it::class.simpleName },
        extras = buildMap {
            element?.stringProperty("accessibilityHint")?.let { put("hint", it) }
            (element?.valueForKey("accessibilityTraits") as? NSNumber)?.let { put("traits", it.stringValue) }
            userInfo?.get(UIAccessibilityAssistiveTechnologyKey)?.let { put("assistiveTechnology", it.toString()) }
        },
    )
}

private fun NSNotification.toAnnouncementEvent(): AccessibilityEvent {
    return AccessibilityEvent(
        type = "ANNOUNCEMENT",
        text = userInfo?.get(UIAccessibilityAnnouncementKeyStringValue)?.toString(),
        extras = buildMap {
            userInfo?.get(UIAccessibilityAnnouncementKeyWasSuccessful)?.let { put("success", it.toString()) }
        },
    )
}

// the accessibility properties are declared on an informal protocol, read them with key-value coding
private fun NSObject.stringProperty(key: String): String? =
    (valueForKey(key) as? String)?.takeIf { it.isNotEmpty() }
