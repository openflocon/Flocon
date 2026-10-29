package io.github.openflocon.domain.connect.usecase

import io.github.openflocon.domain.connect.models.LocalAddressDomainModel
import io.github.openflocon.domain.connect.repository.LocalNetworkRepository

class GetLocalAddressesUseCase(
    private val localNetworkRepository: LocalNetworkRepository,
) {
    operator fun invoke(): List<LocalAddressDomainModel> = cleanLocalAddresses(localNetworkRepository.getLocalAddresses())
}

private val ipv4Regex = Regex("""^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$""")

/**
 * Keeps the IPv4 addresses a device can reach from the local network:
 * no loopback, no link-local (169.254.x.x), no duplicates. Private ranges come first.
 */
internal fun cleanLocalAddresses(addresses: List<LocalAddressDomainModel>): List<LocalAddressDomainModel> = addresses
    .filter { address ->
        val match = ipv4Regex.matchEntire(address.ip) ?: return@filter false
        val parts = match.groupValues.drop(1).map { it.toInt() }
        parts.all { it in 0..255 } &&
            parts[0] != 127 &&
            parts[0] != 0 &&
            !(parts[0] == 169 && parts[1] == 254)
    }
    .distinctBy { it.ip }
    .sortedBy { if (it.ip.isPrivateIpv4()) 0 else 1 }

private fun String.isPrivateIpv4(): Boolean {
    val parts = split(".").map { it.toInt() }
    return parts[0] == 10 ||
        (parts[0] == 172 && parts[1] in 16..31) ||
        (parts[0] == 192 && parts[1] == 168)
}
