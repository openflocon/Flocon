package com.flocon.sample.android

import com.posthog.PostHog

/**
 * Routes only purpose-written application log lines to PostHog Logs.
 * Existing application and Flocon loggers remain unchanged.
 */
internal object PostHogAppLogger {
    fun info(message: String, attributes: Map<String, Any>) {
        PostHog.logger.info(message, attributes)
    }

    fun error(message: String, attributes: Map<String, Any>) {
        PostHog.logger.error(message, attributes)
    }
}
