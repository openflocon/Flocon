package io.github.openflocon.flocon.plugins.dashboard.model.config

data class TextConfig(
    val label: String,
    val value: String,
    val color: Int?,
) : ElementConfig

