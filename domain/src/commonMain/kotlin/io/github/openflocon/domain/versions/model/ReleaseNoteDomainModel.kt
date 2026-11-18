package io.github.openflocon.domain.versions.model

data class ReleaseNoteDomainModel(
    val version: String,
    val title: String,
    val body: String,
    val publishedAt: String?,
    val link: String,
)
