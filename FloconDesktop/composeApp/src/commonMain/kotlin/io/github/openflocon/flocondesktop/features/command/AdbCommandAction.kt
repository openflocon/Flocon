package io.github.openflocon.flocondesktop.features.command

internal sealed interface AdbCommandAction {

    data class Create(val command: String) : AdbCommandAction

    data class Execute(val command: AdbCommandUi) : AdbCommandAction

    data class Delete(val id: Long)

}
