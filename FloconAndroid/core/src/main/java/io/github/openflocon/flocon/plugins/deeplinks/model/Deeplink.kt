package io.github.openflocon.flocon.plugins.deeplinks.model

data class Deeplink(
    val link: String,
    val label: String? = null,
    val description: String? = null,
)