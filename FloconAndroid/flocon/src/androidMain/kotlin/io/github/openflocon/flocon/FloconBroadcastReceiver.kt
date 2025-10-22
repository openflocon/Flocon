package io.github.openflocon.flocon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

internal class FloconBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        FloconLogger.log("Started detection broadcast receiver with: ${intent?.action}")

        val serial = intent?.getStringExtra("serial")
        if (serial != null) {
            FloconLogger.log("serial : $serial")

            Flocon.client?.devicePlugin?.registerWithSerial(serial)
        }
    }
}