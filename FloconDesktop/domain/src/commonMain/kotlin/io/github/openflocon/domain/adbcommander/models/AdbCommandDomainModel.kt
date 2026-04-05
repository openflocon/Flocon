package io.github.openflocon.domain.adbcommander.models

data class AdbCommandDomainModel(
    val id: Long,
    val name: String,
    val command: String,
    val description: String?,
)
