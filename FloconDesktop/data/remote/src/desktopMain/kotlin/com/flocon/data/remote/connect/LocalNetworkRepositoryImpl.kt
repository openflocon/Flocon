package com.flocon.data.remote.connect

import io.github.openflocon.domain.connect.models.LocalAddressDomainModel
import io.github.openflocon.domain.connect.repository.LocalNetworkRepository
import java.net.Inet4Address
import java.net.NetworkInterface

internal class LocalNetworkRepositoryImpl : LocalNetworkRepository {

    override fun getLocalAddresses(): List<LocalAddressDomainModel> = try {
        NetworkInterface.getNetworkInterfaces()
            ?.toList()
            .orEmpty()
            .filter { it.isUp && !it.isLoopback && !it.isVirtual }
            .flatMap { networkInterface ->
                networkInterface.inetAddresses.toList()
                    .filterIsInstance<Inet4Address>()
                    .mapNotNull { address ->
                        address.hostAddress?.let {
                            LocalAddressDomainModel(
                                interfaceName = networkInterface.displayName ?: networkInterface.name,
                                ip = it,
                            )
                        }
                    }
            }
    } catch (_: Exception) {
        emptyList()
    }
}
