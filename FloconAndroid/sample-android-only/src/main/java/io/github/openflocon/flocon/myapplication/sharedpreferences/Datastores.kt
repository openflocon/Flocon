package io.github.openflocon.flocon.myapplication.sharedpreferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.openflocon.flocon.plugins.sharedprefs.floconRegisterPreference
import io.github.openflocon.flocon.preferences.datastores.model.FloconDatastorePreference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "datastore")

private val USER_NAME = stringPreferencesKey("username")
private val USER_AGE = intPreferencesKey("user.age")

fun initializeDatastores(context: Context) {
    Datastores(context)
}

class Datastores(private val context: Context) {
    suspend fun saveUsername(username: String) {
        val value = context.dataStore.data.first()[USER_NAME]
        println("Datastore Local Value : $value")
        if (value == null) {
            context.dataStore.edit { prefs ->
                prefs[USER_NAME] = username
            }
        }
    }

    suspend fun saveUserAge(age: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_AGE] = age
        }
    }

    init {
        floconRegisterPreference(FloconDatastorePreference("datastore", context.dataStore))
        GlobalScope.launch {
            saveUsername("John Doe")
            saveUserAge(30)
        }
    }
}
