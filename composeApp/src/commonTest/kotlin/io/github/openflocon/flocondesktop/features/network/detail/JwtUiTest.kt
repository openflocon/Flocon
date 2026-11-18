package io.github.openflocon.flocondesktop.features.network.detail

import io.github.openflocon.flocondesktop.features.network.detail.mapper.formatExpiration
import io.github.openflocon.flocondesktop.features.network.detail.mapper.toJwtUi
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JwtUiTest {

    private val payload = """{"sub":"user_42","exp":1000,"admin":true}"""

    @Test
    fun `decodes the bearer token claims and expiration`() {
        val jwt = toJwtUi(
            headers = mapOf("Authorization" to "Bearer abc.def.ghi"),
            decodeJwtPayload = { payload },
            nowSeconds = 700,
        )

        assertEquals("abc.def.ghi", jwt?.token)
        assertEquals(
            listOf(
                NetworkDetailHeaderUi("sub", "user_42"),
                NetworkDetailHeaderUi("exp", "1000"),
                NetworkDetailHeaderUi("admin", "true"),
            ),
            jwt?.claims?.toList(),
        )
        assertEquals("Expires in 5 min", jwt?.expiration)
        assertFalse(jwt!!.isExpired)
    }

    @Test
    fun `expired token`() {
        val jwt = toJwtUi(
            headers = mapOf("authorization" to "Bearer a.b.c"),
            decodeJwtPayload = { payload },
            nowSeconds = 1_000 + 7_200,
        )

        assertEquals("Expired 2 h ago", jwt?.expiration)
        assertTrue(jwt!!.isExpired)
    }

    @Test
    fun `no jwt without a bearer header or a decodable payload`() {
        assertNull(toJwtUi(null, { payload }, 0))
        assertNull(toJwtUi(mapOf("authorization" to "Basic abc"), { payload }, 0))
        assertNull(toJwtUi(mapOf("authorization" to "Bearer opaque"), { null }, 0))
    }

    @Test
    fun `formats durations`() {
        assertEquals("Expires in 30 s", formatExpiration(30))
        assertEquals("Expires in 3 d", formatExpiration(3 * 86_400L))
        assertEquals("Expired 1 min ago", formatExpiration(-60))
    }
}
