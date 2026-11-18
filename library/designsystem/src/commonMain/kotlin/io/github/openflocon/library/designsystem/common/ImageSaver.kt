package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun saveImageToFile(bitmap: ImageBitmap, defaultFileName: String = "image.png"): Boolean
