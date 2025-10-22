package io.github.openflocon.flocon

import android.content.Context
import android.util.Log
import android.widget.Toast

actual class FloconContext(val appContext: Context)

internal actual fun displayClearTextError(context: FloconContext) {
    Toast.makeText(
        context.appContext,
        "Cannot start Flocon : ClearText Issue, see Logcat",
        Toast.LENGTH_LONG
    ).show()
    Log.e(
        "Flocon",
        "Flocon uses ClearText communication to the server, it seems you already have a network-security-config setup on your project, please ensure you allowed cleartext communication on your debug app https://github.com/openflocon/Flocon?tab=readme-ov-file#-why-flocon-cant-see-your-device-calls-and-how-to-fix-it-"
    )
}