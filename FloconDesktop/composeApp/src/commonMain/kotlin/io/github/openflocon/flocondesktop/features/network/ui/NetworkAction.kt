package io.github.openflocon.flocondesktop.features.network.ui

sealed interface NetworkAction {

    data class SelectRequest(val id: String) : NetworkAction

}
