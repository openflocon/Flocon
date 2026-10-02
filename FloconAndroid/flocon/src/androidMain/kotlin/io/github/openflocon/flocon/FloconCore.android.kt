package io.github.openflocon.flocon

import android.util.Log
import android.widget.Toast

internal actual fun displayClearTextError(context: FloconContext) {
    Toast.makeText(
        /* context = */ context.context,
        /* text = */ "Cannot start Flocon : ClearText Issue, see Logcat",
        /* duration = */ Toast.LENGTH_LONG
    )
        .show()
    Log.e(
        /* tag = */ "Flocon",
        /* msg = */
        "Flocon uses ClearText communication to the server, it seems you already have a network-security-config setup on your project, please ensure you allowed cleartext communication on your debug app https://github.com/openflocon/Flocon?tab=readme-ov-file#-why-flocon-cant-see-your-device-calls-and-how-to-fix-it-"
    )
}