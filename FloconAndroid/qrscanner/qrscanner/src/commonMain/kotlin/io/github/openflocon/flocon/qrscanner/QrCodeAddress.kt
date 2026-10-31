package io.github.openflocon.flocon.qrscanner

/**
 * The address to connect to for the content of a QR code.
 *
 * The content is either the bare address of the computer (`192.168.1.20`), or a URL whose host is that
 * address (`flocon://192.168.1.20`, `wss://my-mac.local:9023`): the scheme, the port and the path are
 * ignored, as the port is always 9023.
 *
 * Anything that is not a URL is returned trimmed and unchanged, so that `Flocon.connect` reports it
 * as an invalid address when it cannot be read.
 */
internal fun addressFromQrCode(content: String): String {
    val text = content.trim()
    val schemeEnd = text.indexOf("://")
    if (schemeEnd <= 0) return text
    val host = text
        .substring(schemeEnd + 3)
        .substringBefore('/')
        .substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('@')
        .substringBefore(':')
    return host.ifEmpty { text }
}
