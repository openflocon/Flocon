package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconLogger
import platform.Foundation.NSFileManager

/**
 * Shows the container of an App Group, shared with the widgets and extensions of the app,
 * in the Files section of the Flocon desktop app.
 *
 * The app must have the App Groups capability with this [identifier]; otherwise nothing is shown.
 */
fun floconRegisterAppGroup(identifier: String) {
    val path = NSFileManager.defaultManager
        .containerURLForSecurityApplicationGroupIdentifier(identifier)
        ?.path
    if (path == null) {
        FloconLogger.log("No container for the App Group $identifier: check the App Groups capability")
        return
    }
    floconRegisterFolder(displayName = "App Group · $identifier", absolutePath = path)
}
