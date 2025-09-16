package io.github.openflocon.domain.deeplink.models

data class DeeplinkDomainModel(
    val id: Long,
    val label: String?,
    val link: String,
    val description: String?,
)
