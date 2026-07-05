package io.github.openflocon.domain.network.models

data class NetworkCallWithDeviceId(
    val deviceId: String,
    val call: FloconNetworkCallDomainModel,
)
