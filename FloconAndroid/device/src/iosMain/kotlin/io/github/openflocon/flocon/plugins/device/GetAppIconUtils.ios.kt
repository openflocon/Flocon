package io.github.openflocon.flocon.plugins.device

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSDataBase64Encoding64CharacterLineLength
import platform.Foundation.base64EncodedStringWithOptions
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

@OptIn(ExperimentalForeignApi::class)
actual fun getAppIconBase64(context: FloconContext): String? {
    return try {
        val icons = NSBundle.mainBundle.infoDictionary
            ?.get("CFBundleIcons") as? Map<*, *>
        val primary = icons?.get("CFBundlePrimaryIcon") as? Map<*, *>
        val iconName = (primary?.get("CFBundleIconName") as? String)
            ?: (primary?.get("CFBundleIconFiles") as? List<*>)?.lastOrNull() as? String
            ?: return null

        val image = UIImage.imageNamed(iconName) ?: return null
        val data = UIImagePNGRepresentation(image) ?: return null
        // no line breaks: the desktop decodes it as a single base64 string
        data.base64EncodedStringWithOptions(0u)
    } catch (t: Throwable) {
        FloconLogger.logError(text = "Error while getting app icon", throwable = t)
        null
    }
}
