package com.flocon.data.remote.settings

import io.github.openflocon.domain.settings.repository.NetworkAddressRepository

internal data class NetworkInterfaceCandidate(
    val isUp: Boolean,
    val isLoopback: Boolean,
    val isVirtual: Boolean,
    val ipv4Addresses: List<String>,
)

/**
 * Keeps the site-local IPv4 addresses of the interfaces that are up, not loopback and not virtual.
 */
internal fun filterLocalNetworkAddresses(candidates: List<NetworkInterfaceCandidate>): List<String> = candidates
    .filter { it.isUp && !it.isLoopback && !it.isVirtual }
    .flatMap { it.ipv4Addresses }
    .filter { isSiteLocalIpv4(it) }
    .distinct()

/** RFC 1918: 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16. */
internal fun isSiteLocalIpv4(address: String): Boolean {
    val parts = address.split(".").map { it.toIntOrNull() ?: return false }
    if (parts.size != 4 || parts.any { it !in 0..255 }) return false
    return when (parts[0]) {
        10 -> true
        172 -> parts[1] in 16..31
        192 -> parts[1] == 168
        else -> false
    }
}

internal expect fun getNetworkAddressRepository(): NetworkAddressRepository
