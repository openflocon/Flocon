package io.github.openflocon.flocon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class FloconBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Log le message pour l'afficher dans Logcat
        Log.d("FloconBroadcastReceiver", "Réception de la diffusion avec l'action: ${intent?.action}")

        val serial = intent?.getStringExtra("serial")
        if (serial != null) {
            Log.d("FloconBroadcastReceiver", "serial : $serial")
        }
    }
}

/*
adb shell am broadcast -a "io.github.openflocon.flocon.DETECT" -n "io.github.openflocon.flocon.myapplication/io.github.openflocon.flocon.FloconBroadcastReceiver" --es "serial" "v859mbnzin5px8dq"
 */