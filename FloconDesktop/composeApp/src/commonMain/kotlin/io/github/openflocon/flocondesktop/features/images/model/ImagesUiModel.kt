package io.github.openflocon.flocondesktop.features.images.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class ImagesUiModel(
    val id: String,
    val url: String,
    val host: String,
    val path: String,
    // "14:53:18"
    val timeMain: String,
    // ".868"
    val timeMillis: String,
    val weight: String?,
    val format: String?,
    val headers: PersistentMap<String, String>,
) {
    val downloadedAt: String get() = timeMain + timeMillis
}

fun previewImagesUiModel(
    url: String = "https://cdn.example.com/images/avatar.png",
    weight: String? = "142 KB",
    format: String? = "PNG",
) = ImagesUiModel(
    id = "$url-0",
    url = url,
    host = url.substringAfter("://").substringBefore('/'),
    path = "/" + url.substringAfter("://").substringAfter('/', ""),
    timeMain = "14:53:18",
    timeMillis = ".868",
    weight = weight,
    format = format,
    headers = persistentMapOf(),
)

fun previewImagesUiModels() = listOf(
    previewImagesUiModel(),
    previewImagesUiModel(
        url = "https://very-long-subdomain.images.example.com/assets/products/2024/summer/collection/hero-banner-large.jpg?width=1200&quality=80&format=auto",
        format = "JPG",
        weight = "1.2 MB",
    ),
    previewImagesUiModel(url = "https://cdn.example.com/icons/logo.svg", weight = null, format = null),
)
