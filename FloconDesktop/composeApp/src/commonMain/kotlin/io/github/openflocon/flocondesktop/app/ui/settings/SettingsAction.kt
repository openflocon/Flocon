package io.github.openflocon.flocondesktop.app.ui.settings

sealed interface SettingsAction {

    data class FontSizeMultiplierChange(val value: Float) : SettingsAction
}
