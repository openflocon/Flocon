package io.github.openflocon.flocon.utils

import kotlin.time.TimeSource

@JsFun("() => Date.now()")
private external fun dateNow(): Double

actual fun currentTimeMillis(): Long = dateNow().toLong()

actual fun currentTimeNanos(): Long = TimeSource.Monotonic.markNow().elapsedNow().inWholeNanoseconds

actual fun createThrowableFromClassName(className: String): Throwable? = RuntimeException("Error: $className")
