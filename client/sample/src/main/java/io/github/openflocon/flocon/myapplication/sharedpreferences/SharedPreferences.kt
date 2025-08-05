package io.github.openflocon.flocon.myapplication.sharedpreferences

import android.content.Context
import androidx.core.content.edit

fun initializeSharedPreferences(context: Context) {
    context.getSharedPreferences("user_pref", Context.MODE_PRIVATE).apply {
        edit {
            putInt("age", 34)
            putBoolean("isHuman", true)
            putString("name", "flo")
        }
    }
    context.getSharedPreferences("settings_pref", Context.MODE_PRIVATE).apply {
        edit {
            putBoolean("isValid", true)
            putString("settings_dummy_variable", "variable value")
            putString("settings_dummy_variable_2", "variable value 2")
        }
    }

}