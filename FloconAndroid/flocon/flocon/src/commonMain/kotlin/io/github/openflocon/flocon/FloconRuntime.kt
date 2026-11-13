package io.github.openflocon.flocon

import io.github.openflocon.flocon.error.pluginNotInitialized
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmStatic

/**
 * The switch that turns Flocon off, for release builds.
 *
 * Flocon is on, unless `flocon-no-op` is in the app: then [startFlocon] starts nothing and every
 * `Flocon.xxxPlugin` is a no-op. On Android, the R8 rule of `flocon-no-op` makes [isEnabled]
 * return `false` at build time, so the code behind it is removed from the release build.
 */
@InternalFloconApi
object FloconRuntime {

    private val noOpMarkerPresent: Boolean by lazy { isFloconNoOpMarkerPresent() }

    @Volatile
    private var enabledOverride: Boolean? = null

    @Volatile
    private var turnedOffByNoOp: Boolean = false

    /** `false` when Flocon is turned off. Must stay a non-inline static method: the R8 rule of `flocon-no-op` targets it. */
    @JvmStatic
    fun isEnabled(): Boolean = enabledOverride ?: !(turnedOffByNoOp || noOpMarkerPresent)

    /**
     * Called by `flocon-no-op` when the app starts, on the platforms where its marker class
     * can't be looked up (iOS, Wasm).
     */
    fun turnOff() {
        turnedOffByNoOp = true
    }

    /** Forces the switch in tests, `null` goes back to the detection. */
    fun overrideEnabledForTests(enabled: Boolean?) {
        enabledOverride = enabled
        if (enabled == null) turnedOffByNoOp = false
    }
}

/** Whether the marker class of `flocon-no-op` is in the app. */
internal expect fun isFloconNoOpMarkerPresent(): Boolean

/**
 * The value of a `Flocon.xxxPlugin` accessor: [noOp] when Flocon is turned off, else the
 * [installed] plugin, or [noOp] with a log when it is not installed.
 * The switch is checked first so that R8 can remove the plugin implementation from the release build.
 */
@InternalFloconApi
inline fun <T : FloconPlugin> floconPlugin(
    pluginName: String,
    noOp: () -> T,
    installed: () -> T?,
): T = if (!FloconRuntime.isEnabled()) {
    noOp()
} else {
    installed() ?: pluginNotInitialized(pluginName, noOp())
}
