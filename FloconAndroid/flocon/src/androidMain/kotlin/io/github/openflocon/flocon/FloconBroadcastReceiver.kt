package io.github.openflocon.flocon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.openflocon.flocon.dsl.FloconMarker

@OptIn(FloconMarker::class)
internal class FloconBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        FloconLogger.log("Started detection broadcast receiver with: ${intent?.action}")

        val serial = intent?.getStringExtra("serial")
        if (serial != null) {
            FloconLogger.log("serial : $serial")

            FloconDeviceSerial.onSerialReceived(serial)
        }
    }
}