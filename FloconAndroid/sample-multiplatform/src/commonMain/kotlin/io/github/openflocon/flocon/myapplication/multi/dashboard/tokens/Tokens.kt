package io.github.openflocon.flocon.myapplication.multi.dashboard.tokens

import kotlinx.coroutines.flow.MutableStateFlow

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val expiration: String,
)

val tokensFlow = MutableStateFlow<Tokens>(
    Tokens(
        accessToken = "1234567890",
        refreshToken = "dndkjcncjzaksp",
        expiration = "12:30:43"
    )
)
