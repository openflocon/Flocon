package io.github.openflocon.flocondesktop.about

import androidx.compose.runtime.Immutable

@Immutable
internal data class Contributor(
    val firstName: String,
    val lastName: String,
    val profile: String,
    val image: String
)

internal val contributors = listOf(
    Contributor(
        firstName = "Florent",
        lastName = "Champigny",
        profile = "https://github.com/florent37",
        image = "https://avatars.githubusercontent.com/u/5754972?v=4"
    ),
    Contributor(
        firstName = "Raphael",
        lastName = "Teyssandier",
        profile = "https://github.com/doTTTTT",
        image = "https://avatars.githubusercontent.com/u/13266870?v=4"
    )
)
