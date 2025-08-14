package io.github.openflocon.flocon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class FloconBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Log le message pour l'afficher dans Logcat
        Log.d("FloconBroadcastReceiver", "Réception de la diffusion avec l'action: ${intent?.action}")
    }
}