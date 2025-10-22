package io.github.openflocon.flocon

actual object FloconLogger {
    actual var enabled = false
    private const val TAG = "FloconLogger"
    
    actual fun logError(text: String, throwable: Throwable?) {
        if(enabled) {
            println("ERROR $TAG: $text")
            throwable?.printStackTrace()
        }
    }
    
    actual fun log(text: String) {
        if(enabled) {
            println("$TAG: $text")
        }
    }
}

