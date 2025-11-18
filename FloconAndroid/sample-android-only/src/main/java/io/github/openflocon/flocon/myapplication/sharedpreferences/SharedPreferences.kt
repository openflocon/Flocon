@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.myapplication.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import io.github.openflocon.flocon.plugins.sharedprefs.FloconSharedPreference
import io.github.openflocon.flocon.plugins.sharedprefs.floconRegisterPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import org.json.JSONArray
import org.json.JSONObject
import kotlin.uuid.Uuid
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

fun initializeSharedPreferencesAfterInit(context: Context) {
    val referencedPref = context.getSharedPreferences("ref_pref", Context.MODE_PRIVATE)
    floconRegisterPreference(FloconSharedPreference("my_custom_name", referencedPref))

    referencedPref.edit {
        putString("works", "yes")
    }
}

fun initializeSharedPreferences(context: Context) {
    PreferenceManager.getDefaultSharedPreferences(context).edit { putFloat("flocon_validity", 42f) }

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

    context.getSharedPreferences("groups_pref", Context.MODE_PRIVATE).apply {
        edit {
            putString("group_one", generateUsersJson(3).toString(4))
            putString("group_two", generateUsersJson(5).toString(4))
            putString("group_three", generateUsersJson(10).toString(4))
            putString("group_four", generateUsersJson(2).toString(4))
        }
    }
}

private fun generateUsersJson(number: Int) : JSONArray {
    val usersArray = JSONArray()

    for (i in 1..number) {
        val randomUsername = "user_${Uuid.random().toString().substring(0, 8)}"
        val randomEmail = "$randomUsername@example.com"
        val isActive = Random.nextBoolean()

        val userObject = JSONObject()
        userObject.put("id", i)
        userObject.put("username", randomUsername)
        userObject.put("email", randomEmail)
        userObject.put("is_active", isActive)

        usersArray.put(userObject)
    }

    return usersArray
}