package io.github.openflocon.flocon.plugins.sharedprefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.preference.PreferenceManager
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import java.io.File

internal object SharedPreferencesFinder {

    private const val SHARED_PREFS_DIR: String = "shared_prefs"
    private const val XML_SUFFIX: String = ".xml"

    fun buildDescriptorForAllPrefsFiles(
        context: Context
    ): List<FloconPreference> {
        val descriptors = mutableListOf<FloconPreference>()

        val dir = File(context.applicationInfo.dataDir, SHARED_PREFS_DIR)
        val list = dir.list { dir, name -> name.endsWith(XML_SUFFIX) }
        if (list != null) {
            for (each in list) {
                val prefName = each.substring(0, each.indexOf(".xml"))
                descriptors.add(
                    FloconSharedPreference(prefName, sharedPreferences = context.getSharedPreferences(prefName, MODE_PRIVATE)
                ))
            }
        }

        val defaultSharedPrefName = getDefaultSharedPreferencesName(context)
        descriptors.add(
            FloconSharedPreference(
                name = defaultSharedPrefName,
                sharedPreferences = context.getSharedPreferences(defaultSharedPrefName, MODE_PRIVATE)
            )
        )

        return descriptors
    }

    private fun getDefaultSharedPreferencesName(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            PreferenceManager.getDefaultSharedPreferencesName(context)
        else
            context.packageName + "_preferences"
    }

}