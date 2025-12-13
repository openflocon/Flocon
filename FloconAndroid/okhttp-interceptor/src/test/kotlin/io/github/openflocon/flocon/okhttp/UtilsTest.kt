package io.github.openflocon.flocon.okhttp

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.ByteString.Companion.decodeHex
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun `extractResponseBodyInfo decodes brotli stream`() {
        // "Hello, Brotli!" compressed with Brotli
        val brotliHex = "8b068048656c6c6f2c2042726f746c692103"
        val brotliBytes = brotliHex.decodeHex().toByteArray()

        val responseBody = brotliBytes.toResponseBody("application/json".toMediaType())
        
        val response = Response.Builder()
            .request(Request.Builder().url("https://example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .header("Content-Encoding", "br")
            .body(responseBody)
            .build()

        val (bodyString, bodySize) = extractResponseBodyInfo(response, response.headers.toMap())

        assertEquals("Hello, Brotli!", bodyString)
    }

    @Test
    fun `extractResponseBodyInfo handles plain text`() {
        val plainText = "Hello, World!"
        val responseBody = plainText.toResponseBody("text/plain".toMediaType())

        val response = Response.Builder()
            .request(Request.Builder().url("https://example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseBody)
            .build()

        val (bodyString, _) = extractResponseBodyInfo(response, response.headers.toMap())

        assertEquals("Hello, World!", bodyString)
    }
}
