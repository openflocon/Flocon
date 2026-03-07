package io.github.openflocon.domain.deeplink.models

data class Deeplinks(
    val deeplinks: List<DeeplinkDomainModel>,
    val variables: List<DeeplinkVariableDomainModel>
)
