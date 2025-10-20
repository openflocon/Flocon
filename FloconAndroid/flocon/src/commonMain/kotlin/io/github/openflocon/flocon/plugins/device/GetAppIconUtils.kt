package io.github.openflocon.flocon.plugins.device

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Base64
import io.github.openflocon.flocon.FloconLogger
import java.io.ByteArrayOutputStream

internal fun getAppIconBase64(context: Context): String? {
    return try {
        val bitmap = getAppIcon(context)
        val resizedBitmap = resizeAppIcon(bitmap, maxSize = 300)
        encodeToBase64(resizedBitmap)
    } catch (t: Throwable) {
        FloconLogger.logError(
            text = "Error while getting app icon",
            throwable = t,
        )
        null
    }
}

private fun getAppIcon(context: Context): Bitmap {
    // 1. Récupération de l'icône en bitmap
    val packageManager = context.packageManager
    val packageName = context.packageName
    val iconDrawable: Drawable = packageManager.getApplicationIcon(packageName)

    val bitmap = Bitmap.createBitmap(
        iconDrawable.intrinsicWidth,
        iconDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    iconDrawable.setBounds(0, 0, canvas.width, canvas.height)
    iconDrawable.draw(canvas)

    return bitmap
}

private fun encodeToBase64(resizedBitmap: Bitmap): String {
    // 3. Conversion en Base64
    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()

    return Base64.encodeToString(byteArray, Base64.NO_WRAP) // NO_WRAP pour éviter les \n
}

private fun resizeAppIcon(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height, 1f)

    return Bitmap.createScaledBitmap(
        bitmap,
        (width * scale).toInt(),
        (height * scale).toInt(),
        true
    )
}
