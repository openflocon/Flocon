package io.github.openflocon.domain.models.settings

enum class ThemeSetting {
    Dark,
    Light,
    System,
    ;

    companion object {
        val DEFAULT = Dark
    }
}
