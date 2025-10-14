package com.flocon.data.remote.network.mapper


import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.getOrNull
import io.github.openflocon.domain.common.success
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.decompressContentIfPossible(headers: Map<String, String>) = decompressContent(
    headers = headers,
).getOrNull() ?: this

/**
 * Decompresses content based on the Content-Encoding header
 *
 * @param content The raw response body content
 * @param headers The response headers map
 * @return Decompressed content as string, or original content if no compression or decompression fails
 */
fun String.decompressContent(headers: Map<String, String>): Either<Throwable, String> {
    val content = this

    if (content.isEmpty()) return content.success()

    val contentEncoding = headers.entries
        .find { it.key.lowercase() == "content-encoding" }
        ?.value?.lowercase()

    return when (contentEncoding) {
        "gzip" -> decompressGzip(content)
        "deflate" -> decompressDeflate(content)
        else -> content.success()
    }
}

/**
 * Attempts to decompress gzipped content
 *
 * @param content Base64 encoded or raw gzipped content
 * @return Decompressed string or null if decompression fails
 */
@OptIn(ExperimentalEncodingApi::class)
private fun decompressGzip(content: String): Either<Throwable, String> {
    return try {
        // Try to decode as Base64 first (common case)
        val compressed = try {
            Base64.decode(content)
        } catch (e: Exception) {
            // If Base64 decoding fails, treat as raw bytes
            content.encodeToByteArray()
        }

        decompressGzipBytes(compressed)
    } catch (t: Throwable) {
        t.failure()
    }
}

/**
 * Attempts to decompress deflate content
 *
 * @param content Base64 encoded or raw deflated content
 * @return Decompressed string or null if decompression fails
 */
@OptIn(ExperimentalEncodingApi::class)
private fun decompressDeflate(content: String): Either<Throwable, String> {
    return try {
        // Try to decode as Base64 first (common case)
        val compressed = try {
            Base64.decode(content)
        } catch (e: Exception) {
            // If Base64 decoding fails, treat as raw bytes
            content.encodeToByteArray()
        }

        decompressDeflateBytes(compressed)
    } catch (t: Throwable) {
        t.failure()
    }
}

expect fun decompressGzipBytes(compressedData: ByteArray): Either<Throwable, String>
expect fun decompressDeflateBytes(compressedData: ByteArray): Either<Throwable, String>
