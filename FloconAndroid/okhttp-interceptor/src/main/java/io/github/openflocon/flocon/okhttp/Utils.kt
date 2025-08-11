package io.github.openflocon.flocon.okhttp

internal fun getHttpMessage(httpCode: Int): String {
    return when (httpCode) {
        // 1xx Informational
        100 -> "Continue"
        101 -> "Switching Protocols"
        103 -> "Early Hints"

        // 2xx Success
        200 -> "OK"
        201 -> "Created"
        202 -> "Accepted"
        204 -> "No Content"
        206 -> "Partial Content"

        // 3xx Redirection
        300 -> "Multiple Choices"
        301 -> "Moved Permanently"
        302 -> "Found"
        304 -> "Not Modified"
        307 -> "Temporary Redirect"
        308 -> "Permanent Redirect"

        // 4xx Client Error
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        405 -> "Method Not Allowed"
        408 -> "Request Timeout"
        409 -> "Conflict"
        410 -> "Gone"
        429 -> "Too Many Requests"

        // 5xx Server Error
        500 -> "Internal Server Error"
        501 -> "Not Implemented"
        502 -> "Bad Gateway"
        503 -> "Service Unavailable"
        504 -> "Gateway Timeout"

        else -> "Unknown"
    }
}