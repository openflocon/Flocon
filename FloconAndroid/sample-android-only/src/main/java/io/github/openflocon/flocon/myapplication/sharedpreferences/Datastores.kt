package io.github.openflocon.flocon.myapplication.sharedpreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.sharedprefs.floconRegisterPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue
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

class FloconDatastorePreference(
    override val name: String,
    private val dataStore: DataStore<Preferences>,
) : FloconPreference {

    override suspend fun set(
        columnName: String,
        value: FloconPreferenceValue
    ) {
        val data = dataStore.data.first().asMap()
        val key = data.keys.find { it.name == columnName } ?: return

        dataStore.edit {
            try {
                when (it[key]) {
                    is String -> it[stringPreferencesKey(columnName)] = value.stringValue!!
                    is Int -> it[intPreferencesKey(columnName)] = value.intValue!!
                    is Boolean -> it[booleanPreferencesKey(columnName)] = value.booleanValue!!
                    is Float -> it[floatPreferencesKey(columnName)] = value.floatValue!!
                    is Long -> it[longPreferencesKey(columnName)] = value.longValue!!
                    is Double -> it[doublePreferencesKey(columnName)] = value.floatValue!!.toDouble()
                }
            } catch (t: Throwable) {
                FloconLogger.logError("cannot update datastore preference",t)
            }
        }
    }

    override suspend fun columns(): List<String> {
        return dataStore.data.first().asMap().map { it.key.name }
    }

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        val data = dataStore.data.first().asMap()
        val key = data.keys.find { it.name == columnName } ?: return null
        val value = data[key] ?: return null

        return when(value) {
            is String -> FloconPreferenceValue(stringValue = value)
            is Int -> FloconPreferenceValue(intValue = value)
            is Float -> FloconPreferenceValue(floatValue = value)
            is Double -> FloconPreferenceValue(floatValue = value.toFloat())
            is Boolean -> FloconPreferenceValue(booleanValue = value)
            is Long -> FloconPreferenceValue(longValue = value)
            else -> null
        }
    }

}