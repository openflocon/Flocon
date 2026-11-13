package io.github.openflocon.domain.versions.model

sealed interface IsLastVersionDomainModel {
    data object RunningLastVersion : IsLastVersionDomainModel
    data class NewVersionAvailable(
        val name: String,
        val oldVersion: String,
        val link: String,
        /** The SDK that has a newer version, null for an update of the desktop app. */
        val sdk: FloconSdk? = null,
    ) : IsLastVersionDomainModel
}
