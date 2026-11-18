package io.github.openflocon.domain.log

/**
 * Writes the logs displayed in the Flocon logs screen
 */
interface LogManager {
    fun d(tag: String, message: String, domain: LogDomain)
    fun i(tag: String, message: String, domain: LogDomain)
    fun e(tag: String, message: String, throwable: Throwable?, domain: LogDomain)
}
