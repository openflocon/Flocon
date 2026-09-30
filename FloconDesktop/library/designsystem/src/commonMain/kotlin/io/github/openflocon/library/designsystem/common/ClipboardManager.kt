package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap

expect fun copyToClipboard(text: String)

expect fun copyToClipboard(bitmap: ImageBitmap)

expect fun readFromClipboard(): String?

