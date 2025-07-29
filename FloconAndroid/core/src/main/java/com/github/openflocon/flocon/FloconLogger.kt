package com.github.openflocon.flocon

import android.util.Log

object FloconLogger {
    private const val enabled = false
    private const val TAG = "Flocon"
    fun logError(text: String, throwable: Throwable?) {
        Log.e(TAG, text, throwable)
    }
    fun log(text: String) {
        Log.d(TAG, text)
    }
}