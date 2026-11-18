package io.github.openflocon.flocondesktop.features.network.detail

sealed interface NetworkDetailEvent {

    data class CreateHtmlFile(val html: String) : NetworkDetailEvent
}
