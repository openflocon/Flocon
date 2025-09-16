package io.github.openflocon.flocon

import android.util.Log

object FloconLogger {
    var enabled = true
    private const val TAG = "FloconLogger"
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