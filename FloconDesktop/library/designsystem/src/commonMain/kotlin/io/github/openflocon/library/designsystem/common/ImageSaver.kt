package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap

expect fun saveImageToFile(bitmap: ImageBitmap, defaultFileName: String = "image.png"): Boolean
