package io.github.openflocon.flocon

import android.util.Log

actual object FloconLogger {
    actual var enabled = false
    private const val TAG = "FloconLogger"
    
    actual fun logError(text: String, throwable: Throwable?) {
        if(enabled) {
            Log.e(TAG, text, throwable)
        }
    }
    
    actual fun log(text: String) {
        if(enabled) {
            Log.d(TAG, text)
        }
    }
}

