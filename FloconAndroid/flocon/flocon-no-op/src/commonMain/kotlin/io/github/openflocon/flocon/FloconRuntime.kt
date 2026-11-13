package io.github.openflocon.flocon

import kotlin.jvm.JvmStatic

/**
 * No-op version: Flocon is always turned off.
 */
@InternalFloconApi
object FloconRuntime {

    @JvmStatic
    fun isEnabled(): Boolean = false

    fun overrideEnabledForTests(enabled: Boolean?) {
        // no op
    }
}

/** No-op version: always [noOp]. */
@InternalFloconApi
inline fun <T : FloconPlugin> floconPlugin(
    pluginName: String,
    noOp: () -> T,
    installed: () -> T?,
): T = noOp()
