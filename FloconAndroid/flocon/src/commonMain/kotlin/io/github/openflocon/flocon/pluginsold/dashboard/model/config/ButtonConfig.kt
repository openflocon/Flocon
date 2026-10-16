package io.github.openflocon.flocon.pluginsold.dashboard.model.config

data class ButtonConfig(
    val text: String,
    val id: String,
    val onClick: () -> Unit,
) : ElementConfig