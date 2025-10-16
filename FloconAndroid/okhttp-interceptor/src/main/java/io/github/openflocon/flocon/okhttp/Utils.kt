package io.github.openflocon.flocon.okhttp

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.nio.charset.Charset

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


internal fun MediaType?.charsetOrUtf8(): Charset = this?.charset() ?: Charsets.UTF_8

internal fun extractResponseBodyInfo(
    response: Response,
    responseHeaders: Map<String, String>
): Pair<String?, Long?> {
    val responseBody = response.body ?: return null to null

    var bodyString: String? = null
    var bodySize: Long? = null

    val source = responseBody.source()
    source.request(Long.MAX_VALUE) // Buffer the entire body, otherwise we have an empty string

    var buffer = source.buffer

    val charset = responseBody.contentType().charsetOrUtf8()

    val contentEncoding = responseHeaders["Content-Encoding"] ?: responseHeaders["content-encoding"]
    if ("gzip".equals(contentEncoding, ignoreCase = true)) {
        GzipSource(buffer.clone()).use { gzippedResponseBody ->
            buffer = Buffer()
            buffer.writeAll(gzippedResponseBody)
        }

        bodyString = buffer.clone().readString(charset)
        bodySize = buffer.size
    } else {
        bodyString = buffer.clone().readString(charset)
        bodySize = buffer.size
    }

    return bodyString to bodySize
}

internal fun extractRequestBodyInfo(
    request: Request,
    requestHeaders: Map<String, String>
): Pair<String?, Long?> {
    val requestBody = request.body ?: return null to null

    var bodySize: Long? = null

    var buffer = Buffer()
    requestBody.writeTo(buffer)

    val contentEncoding = requestHeaders["Content-Encoding"] ?: requestHeaders["content-encoding"]

    if ("gzip".equals(contentEncoding, ignoreCase = true)) {
        bodySize = buffer.size
        GzipSource(buffer).use { gzippedResponseBody ->
            buffer = Buffer()
            buffer.writeAll(gzippedResponseBody)
        }
    }

    val charset = requestBody.contentType().charsetOrUtf8()
    val bodyString = buffer.readString(charset)

    return bodyString to bodySize
}