package io.github.openflocon.domain.settings.repository

interface NetworkAddressRepository {
    /**
     * The site-local IPv4 addresses (e.g. 192.168.1.20) of this machine, on which apps can reach the server.
     */
    fun getLocalAddresses(): List<String>
}
