package io.github.openflocon.flocondesktop.features.network.options

// the options of a network call, in the order of the menus
enum class NetworkItemOption {
    CopyUrl,
    CopyCurl,
    CreateMock,
    Replay,
    ShareAsMarkdown,
}

/**
 * The options offered for a call. Only the url can be copied for a gRPC or a WebSocket call: there is no http request
 * to rebuild, replay or mock.
 */
fun networkItemOptions(httpActions: Boolean): List<NetworkItemOption> = if (httpActions) {
    NetworkItemOption.entries
} else {
    listOf(NetworkItemOption.CopyUrl)
}
