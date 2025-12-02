package io.github.openflocon.flocondesktop.features.network.badquality.edition.model

data class PossibleExceptionUiModel(
    val classPath: String,
    val description: String,
)

// warning : be sure it's only IOExceptions
val possibleExceptions = listOf(
    PossibleExceptionUiModel(
        "java.net.SocketTimeoutException",
        "The network operation timed out (connection, read, or write)."
    ),
    PossibleExceptionUiModel(
        "java.net.UnknownHostException",
        "Unable to resolve the host name or server address."
    ),
    PossibleExceptionUiModel(
        "java.net.ConnectException",
        "Connection to the server was refused or couldn't be established."
    ),
    PossibleExceptionUiModel(
        "javax.net.ssl.SSLHandshakeException",
        "Failed to complete the TLS/SSL handshake (often a certificate issue)."
    ),
    PossibleExceptionUiModel(
        "java.io.IOException",
        "A generic I/O error; an unspecified network problem."
    )
)
