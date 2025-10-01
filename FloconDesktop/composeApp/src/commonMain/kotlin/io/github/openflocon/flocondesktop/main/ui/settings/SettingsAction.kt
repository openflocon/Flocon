package io.github.openflocon.flocondesktop.main.ui.settings

sealed interface SettingsAction {

    data class FontSizeMultiplierChange(val value: Float) : SettingsAction

}
