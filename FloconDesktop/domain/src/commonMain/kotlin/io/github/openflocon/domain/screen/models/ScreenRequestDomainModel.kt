package io.github.openflocon.domain.screen.models

data class ScreenRequestDomainModel(
    val screen: FloconScreen,
    val requestedAtMs: Long,
)
