package io.github.openflocon.flocon.plugins.dashboard.model.config

data class PlainTextConfig(
    val label: String,
    val value: String,
    val type: String, // text, json
) : ElementConfig

