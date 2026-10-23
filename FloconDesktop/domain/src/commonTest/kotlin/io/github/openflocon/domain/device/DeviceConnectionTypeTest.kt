package io.github.openflocon.domain.device

import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel.Adb
import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel.Network
import io.github.openflocon.domain.device.models.connectionTypeOf
import io.github.openflocon.domain.device.models.isLoopbackAddress
import io.github.openflocon.domain.device.models.mergeConnectionTypes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceConnectionTypeTest {

    @Test
    fun `loopback addresses are detected`() {
        listOf("127.0.0.1", "localhost", "::1", "0:0:0:0:0:0:0:1").forEach {
            assertTrue(isLoopbackAddress(it), it)
        }
        assertFalse(isLoopbackAddress("192.168.1.42"))
    }

    @Test
    fun `a loopback device is adb and any other is network`() {
        assertEquals(Adb, connectionTypeOf("127.0.0.1"))
        assertEquals(Network, connectionTypeOf("192.168.1.42"))
    }

    @Test
    fun `the live connection wins over the last known one`() {
        val merged = mergeConnectionTypes(
            activeAddresses = mapOf("phone" to "192.168.1.42"),
            lastTypes = mapOf("phone" to Adb, "tablet" to Network),
        )

        assertEquals(mapOf("phone" to Network, "tablet" to Network), merged)
    }

    @Test
    fun `an offline device keeps its last connection type`() {
        val merged = mergeConnectionTypes(
            activeAddresses = emptyMap(),
            lastTypes = mapOf("tablet" to Network),
        )

        assertEquals(mapOf("tablet" to Network), merged)
    }
}
