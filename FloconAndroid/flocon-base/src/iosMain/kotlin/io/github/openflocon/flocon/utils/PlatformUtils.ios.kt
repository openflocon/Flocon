package io.github.openflocon.flocon.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun createThrowableFromClassName(className: String): Throwable? {
    // iOS doesn't support dynamic class loading like JVM
    // Return a generic exception
    return RuntimeException("Error: $className")
}

