package io.github.openflocon.flocondesktop.features.images.mapper

import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import kotlinx.collections.immutable.toPersistentMap

internal fun List<DeviceImageDomainModel>.filterBy(filter: String): List<DeviceImageDomainModel> = if (filter.isEmpty()) {
    this
} else {
    this.filter {
        it.url.contains(filter)
    }
}

internal fun DeviceImageDomainModel.toUi(): ImagesUiModel {
    val (host, path) = splitUrl(url)
    val (timeMain, timeMillis) = splitTime(formatTimestamp(time))
    return ImagesUiModel(
        id = "$url-$time",
        url = url,
        host = host,
        path = path,
        timeMain = timeMain,
        timeMillis = timeMillis,
        weight = byteSize?.let(::formatImageWeight),
        format = imageFormat(contentType = contentType, url = url),
        headers = headers.toPersistentMap(),
    )
}

/** Strips the scheme, then splits into host and path (path keeps its leading "/" and the query). */
internal fun splitUrl(url: String): Pair<String, String> {
    val withoutScheme = url.substringAfter("://", url)
    val pathStart = withoutScheme.indexOfFirst { it == '/' || it == '?' || it == '#' }
    return if (pathStart < 0) {
        withoutScheme to ""
    } else {
        withoutScheme.substring(0, pathStart) to withoutScheme.substring(pathStart)
    }
}

/** "14:53:18.868" -> "14:53:18" and ".868". */
internal fun splitTime(formatted: String): Pair<String, String> {
    val dot = formatted.lastIndexOf('.')
    return if (dot < 0) {
        formatted to ""
    } else {
        formatted.substring(0, dot) to formatted.substring(dot)
    }
}

/** "142 KB", "1.2 MB". */
internal fun formatImageWeight(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = listOf("KB", "MB", "GB")
    var value = bytes.toDouble() / 1024
    var unit = 0
    while (value >= 1024 && unit < units.lastIndex) {
        value /= 1024
        unit++
    }
    return if (value >= 100) {
        "${value.toLong()} ${units[unit]}"
    } else {
        val tenths = (value * 10).toLong()
        "${tenths / 10}.${tenths % 10} ${units[unit]}"
    }
}

/** "image/svg+xml" -> "SVG", falling back on the url extension, null when unknown. */
internal fun imageFormat(contentType: String?, url: String): String? {
    val fromContentType = contentType
        ?.substringBefore(';')
        ?.trim()
        ?.lowercase()
        ?.substringAfter("image/", "")
        ?.substringBefore('+')
        ?.takeIf { it.isNotEmpty() }
        ?.let(::normalizeFormat)
    if (fromContentType != null) return fromContentType

    val path = url.substringBefore('?').substringBefore('#')
    val extension = path.substringAfterLast('/').substringAfterLast('.', "")
    return extension.takeIf { it.isNotEmpty() && it.length <= 5 }?.let(::normalizeFormat)
}

private val KnownFormats = setOf("jpg", "png", "webp", "gif", "svg", "avif", "bmp", "ico", "heic", "tiff")

private fun normalizeFormat(raw: String): String? {
    val value = when (val lower = raw.lowercase()) {
        "jpeg", "pjpeg" -> "jpg"
        "x-icon", "vnd.microsoft.icon" -> "ico"
        "tif" -> "tiff"
        else -> lower
    }
    return value.takeIf { it in KnownFormats }?.uppercase()
}
