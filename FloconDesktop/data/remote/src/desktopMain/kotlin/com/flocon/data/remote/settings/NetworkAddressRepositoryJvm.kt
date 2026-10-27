package com.flocon.data.remote.settings

import io.github.openflocon.domain.settings.repository.NetworkAddressRepository
import java.net.Inet4Address
import java.net.NetworkInterface

internal actual fun getNetworkAddressRepository(): NetworkAddressRepository = NetworkAddressRepositoryJvm()

internal class NetworkAddressRepositoryJvm : NetworkAddressRepository {

    override fun getLocalAddresses(): List<String> = try {
        val candidates = NetworkInterface.getNetworkInterfaces()
            ?.toList()
            .orEmpty()
            .map { networkInterface ->
                NetworkInterfaceCandidate(
                    isUp = networkInterface.isUp,
                    isLoopback = networkInterface.isLoopback,
                    isVirtual = networkInterface.isVirtual,
                    ipv4Addresses = networkInterface.inetAddresses
                        .toList()
                        .filterIsInstance<Inet4Address>()
                        .mapNotNull { it.hostAddress },
                )
            }
        filterLocalNetworkAddresses(candidates)
    } catch (_: Exception) {
        emptyList()
    }
}
