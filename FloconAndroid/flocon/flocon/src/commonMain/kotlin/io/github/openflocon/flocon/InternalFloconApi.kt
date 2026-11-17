package io.github.openflocon.flocon

/**
 * Marks the Flocon API shared between the Flocon modules. It is not meant to be used by apps
 * and can change without notice.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Internal Flocon API, used between the Flocon modules. It can change without notice.",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class InternalFloconApi
