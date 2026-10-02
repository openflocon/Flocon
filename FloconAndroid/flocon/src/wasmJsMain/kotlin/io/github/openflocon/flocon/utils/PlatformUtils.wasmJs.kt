package io.github.openflocon.flocon.utils

// Using a simple polyfill/wrapper for time in wasmJs if needed, 
// but for now we'll try to use what's available or stubs to make it compile.
// Note: In a real app, you'd use a library like kotlinx-datetime.

actual fun currentTimeMillis(): Long = 0L // Stub for now to ensure compilation

actual fun currentTimeNanos(): Long = 0L // Stub for now to ensure compilation

actual fun createThrowableFromClassName(className: String): Throwable? = null
