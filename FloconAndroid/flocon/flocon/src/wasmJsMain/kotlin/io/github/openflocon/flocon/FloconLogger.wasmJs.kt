package io.github.openflocon.flocon

actual object FloconLogger {
    actual var enabled = false

    actual fun logError(text: String, throwable: Throwable?) {
        if(enabled) {
            println("ERROR: $text")
            throwable?.printStackTrace()
        }
    }

    actual fun log(text: String) {
        if(enabled) {
            println("DEBUG: $text")
        }
    }
}
