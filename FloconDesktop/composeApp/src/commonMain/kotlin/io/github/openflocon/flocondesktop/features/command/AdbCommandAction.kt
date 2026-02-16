package io.github.openflocon.flocondesktop.features.command

sealed interface AdbCommandAction {

    data class Create(val command: String) : AdbCommandAction

    data class Execute(val command: AdbCommandUi) : AdbCommandAction

    data class Delete(val command: AdbCommandUi) : AdbCommandAction
}
