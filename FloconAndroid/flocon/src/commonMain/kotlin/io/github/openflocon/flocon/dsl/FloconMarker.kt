package io.github.openflocon.flocon.dsl

@RequiresOptIn(
    message = "Used to mark internal Flocon APIs",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
annotation class FloconMarker
