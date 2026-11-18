package io.github.openflocon.mcp.network

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.mcp.httpCall
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkMcpMapperTest {

    @Test
    fun `http states follow the network screen rules`() {
        assertEquals(NetworkCallState.Success, httpCall("1", httpCode = 204).state())
        assertEquals(NetworkCallState.Success, httpCall("1", httpCode = 302).state())
        assertEquals(NetworkCallState.Error, httpCall("1", httpCode = 404).state())
        assertEquals(NetworkCallState.Pending, httpCall("1", httpCode = null).state())
    }

    @Test
    fun `failure is an exception`() {
        val call = httpCall("1").copy(
            response = FloconNetworkCallDomainModel.Response.Failure(
                durationMs = 5.0,
                durationFormatted = "5ms",
                issue = "UnknownHostException",
                statusFormatted = "Exception",
            ),
        )

        val summary = call.toSummaryMcpModel(currentSessionId = 1)

        assertEquals("exception", summary.state)
        assertEquals("UnknownHostException", summary.error)
        assertNull(summary.httpCode)
    }

    @Test
    fun `grpc and graphql errors`() {
        val grpc = httpCall("1").copy(
            response = (httpCall("1").response as FloconNetworkCallDomainModel.Response.Success).copy(
                specificInfos = FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc(grpcStatus = "UNAVAILABLE"),
            ),
        )
        val graphQl = httpCall("1").copy(
            response = (httpCall("1").response as FloconNetworkCallDomainModel.Response.Success).copy(
                specificInfos = FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl(httpCode = 200, isSuccess = false),
            ),
        )

        assertEquals(NetworkCallState.Error, grpc.state())
        assertEquals(NetworkCallState.Error, graphQl.state())
    }

    @Test
    fun `summary contains the main infos`() {
        val summary = httpCall("call-1", method = "POST", startTime = 0, appInstance = 2, httpCode = 201)
            .toSummaryMcpModel(currentSessionId = 3)

        assertEquals("call-1", summary.callId)
        assertEquals("http", summary.type)
        assertEquals("POST", summary.method)
        assertEquals("1970-01-01T00:00:00Z", summary.startTime)
        assertEquals(201, summary.httpCode)
        assertEquals(12.0, summary.durationMs)
        assertFalse(summary.isCurrentSession)
    }

    @Test
    fun `bodies are truncated`() {
        val body = "a".repeat(150)

        val truncated = body.toBodyMcpModel(maxChars = 100)!!
        assertEquals(100, truncated.content.length)
        assertTrue(truncated.truncated)
        assertEquals(150, truncated.totalLength)

        assertFalse(body.toBodyMcpModel(maxChars = 1000)!!.truncated)
        assertNull("".toBodyMcpModel(maxChars = 1000))
        assertNull(null.toBodyMcpModel(maxChars = 1000))
    }

    @Test
    fun `detail contains headers, bodies and curl`() {
        val detail = httpCall("1", requestBody = """{"id":1}""").toDetailMcpModel(
            target = DeviceIdAndPackageNameDomainModel("pixel", "io.flocon.sample", 1),
            maxBodyChars = 1000,
            curl = "curl",
        )

        assertEquals(mapOf("Authorization" to "Bearer token"), detail.request.headers)
        assertEquals("""{"id":1}""", detail.request.body?.content)
        assertEquals("""{"name":"flocon"}""", detail.response?.body?.content)
        assertEquals("application/json", detail.response?.contentType)
        assertEquals("curl", detail.curl)
    }
}
