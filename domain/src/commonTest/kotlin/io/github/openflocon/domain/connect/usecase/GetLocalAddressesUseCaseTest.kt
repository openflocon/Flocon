package io.github.openflocon.domain.connect.usecase

import io.github.openflocon.domain.connect.models.LocalAddressDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetLocalAddressesUseCaseTest {

    private fun address(ip: String, name: String = "en0") = LocalAddressDomainModel(name, ip)

    @Test
    fun `empty list stays empty`() {
        assertTrue(cleanLocalAddresses(emptyList()).isEmpty())
    }

    @Test
    fun `loopback, link-local and non ipv4 addresses are removed`() {
        val result = cleanLocalAddresses(
            listOf(
                address("127.0.0.1", "lo0"),
                address("169.254.10.2"),
                address("fe80::1"),
                address("not an ip"),
                address("300.1.1.1"),
                address("192.168.1.20"),
            ),
        )
        assertEquals(listOf("192.168.1.20"), result.map { it.ip })
    }

    @Test
    fun `duplicates are removed`() {
        val result = cleanLocalAddresses(listOf(address("192.168.1.20", "en0"), address("192.168.1.20", "en1")))
        assertEquals(1, result.size)
    }

    @Test
    fun `private addresses come first`() {
        val result = cleanLocalAddresses(
            listOf(address("8.8.4.4"), address("172.20.0.5"), address("10.0.0.2"), address("172.40.0.1")),
        )
        assertEquals(listOf("172.20.0.5", "10.0.0.2", "8.8.4.4", "172.40.0.1"), result.map { it.ip })
    }
}
