package io.github.openflocon.flocon

import android.util.Log

object FloconLogger {
    private const val enabled = false
    private const val TAG = "Flocon"
    fun logError(text: String, throwable: Throwable?) {
        if(enabled) {
            Log.e(TAG, text, throwable)
        }
    }
    fun log(text: String) {
        if(enabled) {
            Log.d(TAG, text)
        }
    }
}