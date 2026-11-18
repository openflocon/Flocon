package io.github.openflocon.domain.connect.repository

import io.github.openflocon.domain.connect.models.LocalAddressDomainModel

interface LocalNetworkRepository {
    fun getLocalAddresses(): List<LocalAddressDomainModel>
}
