package io.github.openflocon.flocon.preferences.datastores.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue
import kotlinx.coroutines.flow.first

interface FloconDatastoreMapper {
    fun fromDatastore(datastoreValue: String) : String
    fun toDatastore(valueForDatastore: String) : String
}

class FloconDatastorePreference(
    override val name: String,
    val dataStore: DataStore<Preferences>,
) : FloconPreference {

    override suspend fun set(
        columnName: String,
        value: FloconPreferenceValue
    ) {
        // no op
    }

    override suspend fun columns(): List<String> {
        return emptyList() // no op
    }

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        return null // no op
    }

}