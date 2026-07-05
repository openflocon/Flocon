package io.github.openflocon.domain.adbcommander.models

data class AdbCommandHistoryDomainModel(
    val id: Long,
    val command: String,
    val output: String,
    val isSuccess: Boolean,
    val executedAt: Long,
)
