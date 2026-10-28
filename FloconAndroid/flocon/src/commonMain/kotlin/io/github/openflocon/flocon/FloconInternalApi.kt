package io.github.openflocon.flocon

/**
 * Used by the optional `flocon-connect` module, which exposes it as `Flocon.connect`, `connectLater`
 * and `Flocon.connectionState`. Not meant to be called directly.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Internal Flocon API, use the flocon-connect module instead.",
)
@Retention(AnnotationRetention.BINARY)
annotation class FloconInternalApi
