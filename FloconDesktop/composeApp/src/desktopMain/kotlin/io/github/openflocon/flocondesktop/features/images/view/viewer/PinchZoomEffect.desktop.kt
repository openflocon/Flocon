package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.LocalAwtWindow
import java.lang.reflect.Proxy
import javax.swing.JComponent
import javax.swing.RootPaneContainer

private const val GESTURE_PACKAGE = "com.apple.eawt.event"

/**
 * Compose desktop does not deliver the macOS trackpad pinch. The JDK exposes it through
 * com.apple.eawt.event (not exported: the app is launched with --add-exports, see build.gradle.kts).
 * Everything goes through reflection so this compiles without the export and is a no-op when it is missing.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PinchZoomEffect(onPinch: (zoomFactor: Float) -> Unit) {
    val currentOnPinch = rememberUpdatedState(onPinch)
    val window = LocalAwtWindow.current

    DisposableEffect(window) {
        val target = (window as? RootPaneContainer)?.rootPane
        val registration = if (target != null && isMac()) {
            registerMagnificationListener(target) { factor -> currentOnPinch.value(factor) }
        } else {
            null
        }
        onDispose { registration?.invoke() }
    }
}

private fun isMac() = System.getProperty("os.name").orEmpty().startsWith("Mac", ignoreCase = true)

/** Returns a function removing the listener, or null when the gesture API is unavailable. */
private fun registerMagnificationListener(
    component: JComponent,
    onFactor: (Float) -> Unit,
): (() -> Unit)? = try {
    val listenerClass = Class.forName("$GESTURE_PACKAGE.MagnificationListener")
    val gestureListenerClass = Class.forName("$GESTURE_PACKAGE.GestureListener")
    val utilities = Class.forName("$GESTURE_PACKAGE.GestureUtilities")
    val eventClass = Class.forName("$GESTURE_PACKAGE.MagnificationEvent")
    val getMagnification = eventClass.getMethod("getMagnification")

    val listener = Proxy.newProxyInstance(
        listenerClass.classLoader,
        arrayOf(listenerClass),
    ) { proxy, method, args ->
        when (method.name) {
            "magnify" -> {
                try {
                    val magnification = getMagnification.invoke(args[0]) as Double
                    onFactor((1.0 + magnification).toFloat())
                } catch (_: Throwable) {
                    // ignore a malformed event
                }
                null
            }

            "hashCode" -> System.identityHashCode(proxy)

            "equals" -> proxy === args?.firstOrNull()

            "toString" -> "FloconMagnificationListener"

            else -> null
        }
    }

    utilities.getMethod("addGestureListenerTo", JComponent::class.java, gestureListenerClass)
        .invoke(null, component, listener)

    val remove = utilities.getMethod("removeGestureListenerFrom", JComponent::class.java, gestureListenerClass)
    (
        {
            try {
                remove.invoke(null, component, listener)
            } catch (_: Throwable) {
                // window already gone
            }
        }
        )
} catch (t: Throwable) {
    println("Pinch to zoom is unavailable: $t")
    null
}
