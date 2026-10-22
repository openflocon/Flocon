package com.flocon.data.remote.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalNetworkAddressesTest {

    private fun candidate(
        vararg addresses: String,
        isUp: Boolean = true,
        isLoopback: Boolean = false,
        isVirtual: Boolean = false,
    ) = NetworkInterfaceCandidate(isUp, isLoopback, isVirtual, addresses.toList())

    @Test
    fun `keeps site-local addresses of a regular up interface`() {
        val result = filterLocalNetworkAddresses(listOf(candidate("192.168.1.20", "10.0.0.5", "172.16.3.4")))

        assertEquals(listOf("192.168.1.20", "10.0.0.5", "172.16.3.4"), result)
    }

    @Test
    fun `drops down, loopback and virtual interfaces`() {
        val result = filterLocalNetworkAddresses(
            listOf(
                candidate("192.168.1.20", isUp = false),
                candidate("192.168.1.21", isLoopback = true),
                candidate("192.168.1.22", isVirtual = true),
                candidate("192.168.1.23"),
            )
        )

        assertEquals(listOf("192.168.1.23"), result)
    }

    @Test
    fun `drops public, link-local and loopback addresses`() {
        val result = filterLocalNetworkAddresses(listOf(candidate("8.8.8.8", "169.254.1.1", "127.0.0.1", "172.32.0.1")))

        assertEquals(emptyList(), result)
    }

    @Test
    fun `removes duplicates`() {
        val result = filterLocalNetworkAddresses(listOf(candidate("192.168.1.20"), candidate("192.168.1.20")))

        assertEquals(listOf("192.168.1.20"), result)
    }

    @Test
    fun `site-local check handles range edges and invalid input`() {
        assertTrue(isSiteLocalIpv4("172.31.255.255"))
        assertFalse(isSiteLocalIpv4("172.15.0.1"))
        assertFalse(isSiteLocalIpv4("192.169.0.1"))
        assertFalse(isSiteLocalIpv4("not.an.ip.address"))
        assertFalse(isSiteLocalIpv4("10.0.0"))
        assertFalse(isSiteLocalIpv4("10.0.0.300"))
    }
}
