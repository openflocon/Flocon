package io.github.openflocon.flocon

object FloconLogger {
    var enabled: Boolean = false
    fun logError(text: String, throwable: Throwable?) = Unit
    fun log(text: String) = Unit
}
