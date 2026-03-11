package io.github.openflocon.flocon.preferences.datastores.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.openflocon.flocon.`plugins-old`.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.`plugins-old`.sharedprefs.model.FloconPreferenceValue

interface FloconDatastoreMapper {
    fun fromDatastore(datastoreValue: String) : String
    fun toDatastore(valueForDatastore: String) : String
}

class FloconDatastorePreference(
    override val name: String,
    val dataStore: DataStore<Preferences>,
) : io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconPreference {

    override suspend fun set(
        columnName: String,
        value: io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconPreferenceValue
    ) {
        // no op
    }

    override suspend fun columns(): List<String> {
        return emptyList() // no op
    }

    override suspend fun get(columnName: String): io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconPreferenceValue? {
        return null // no op
    }

}