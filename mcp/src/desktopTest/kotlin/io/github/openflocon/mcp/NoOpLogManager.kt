package io.github.openflocon.mcp

import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.log.LogManager

internal object NoOpLogManager : LogManager {
    override fun d(tag: String, message: String, domain: LogDomain) = Unit

    override fun i(tag: String, message: String, domain: LogDomain) = Unit

    override fun e(tag: String, message: String, throwable: Throwable?, domain: LogDomain) = Unit
}
