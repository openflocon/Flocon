package io.github.openflocon.flocon.plugins.sharedprefs

import android.content.Context
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference

// Got some code from Flipper client
// https://github.com/facebook/flipper/blob/main/android/src/main/java/com/facebook/flipper/plugins/sharedpreferences/SharedPreferencesFlipperPlugin.java

internal class FloconPreferencesDataSourceAndroid(
    private val context: Context,
) : FloconPreferencesDataSource {

    override fun detectLocalPreferences(): List<FloconPreference> {
        return SharedPreferencesFinder.buildDescriptorForAllPrefsFiles(context)
    }
}

internal actual fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource {
    return FloconPreferencesDataSourceAndroid(context = context.appContext)
}