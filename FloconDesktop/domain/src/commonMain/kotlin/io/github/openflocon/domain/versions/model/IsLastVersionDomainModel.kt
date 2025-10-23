package io.github.openflocon.domain.versions.model

sealed interface IsLastVersionDomainModel {
    data object RunningLastVersion : IsLastVersionDomainModel
    data class NewVersionAvailable(val name: String, val link: String): IsLastVersionDomainModel
}
