package io.github.openflocon.flocon.utils

import java.util.UUID

actual fun generateUuid(): String {
    return UUID.randomUUID().toString()
}

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun createThrowableFromClassName(className: String): Throwable? {
    return try {
        val errorClass = Class.forName(className)
        errorClass.newInstance() as? Throwable
    } catch (t: Throwable) {
        null
    }
}

