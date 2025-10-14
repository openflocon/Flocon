package io.github.openflocon.library.designsystem.common

fun String.isImageUrl() = this.let {
    it.startsWith("http", ignoreCase = true) &&
        (it.endsWith(".png", ignoreCase = true)
            || it.endsWith(".jpg", ignoreCase = true)
            || it.endsWith(".jpeg", ignoreCase = true)
            || it.endsWith(".webp", ignoreCase = true)
            || it.endsWith(".gif", ignoreCase = true))
}
