package io.github.openflocon.flocon

expect object FloconLogger {
    var enabled: Boolean
    fun logError(text: String, throwable: Throwable?)
    fun log(text: String)
}