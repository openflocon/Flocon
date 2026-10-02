package io.github.openflocon.flocon.pluginsold.sharedprefs

import android.content.Context
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.pluginsold.sharedprefs.model.FloconPreference

// Got some code from Flipper client
// https://github.com/facebook/flipper/blob/main/android/src/main/java/com/facebook/flipper/plugins/sharedpreferences/SharedPreferencesFlipperPlugin.java

internal class FloconPreferencesDataSourceAndroid(
    private val context: Context,
) : FloconPreferencesDataSource {

    override fun detectLocalPreferences(): List<FloconPreference> {
        return SharedPreferencesFinder.buildDescriptorForAllPrefsFiles(context)
    }
}

interface FloconPreferencesDataSource {
    fun detectLocalPreferences(): List<FloconPreference>
}

//internal actual fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource {
//    return FloconPreferencesDataSourceAndroid(context = context.context)
//}