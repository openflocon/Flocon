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
    ),
    Contributor(
        firstName = "Wouter",
        lastName = "van der Velde",
        profile = "https://github.com/Woutervdvelde",
        image = "https://github.com/Woutervdvelde.png"
    ),
    Contributor(
        firstName = "Gustavo",
        lastName = "de Santos Garcia",
        profile = "https://github.com/gdesantos",
        image = "https://github.com/gdesantos.png"
    ),
    Contributor(
        firstName = "Quentin",
        lastName = "HUET",
        profile = "https://github.com/Karambar",
        image = "https://github.com/Karambar.png"
    ),
    Contributor(
        firstName = "Sebastian",
        lastName = "Neubauer",
        profile = "https://github.com/snappdevelopment",
        image = "https://github.com/snappdevelopment.png"
    ),
    Contributor(
        firstName = "Simon",
        lastName = "Marquis",
        profile = "https://github.com/SimonMarquis",
        image = "https://github.com/SimonMarquis.png"
    ),
    Contributor(
        firstName = "Stephen",
        lastName = "Vinouze",
        profile = "https://github.com/StephenVinouze",
        image = "https://github.com/StephenVinouze.png"
    ),
    Contributor(
        firstName = "Mothana",
        lastName = "Dabash",
        profile = "https://github.com/mdabash",
        image = "https://github.com/mdabash.png"
    )
)
