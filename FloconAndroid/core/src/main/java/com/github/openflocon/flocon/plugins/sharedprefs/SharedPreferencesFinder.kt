package com.github.openflocon.flocon.plugins.sharedprefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.preference.PreferenceManager
import com.github.openflocon.flocon.plugins.sharedprefs.model.SharedPreferencesDescriptor
import java.io.File

internal object SharedPreferencesFinder {

    private const val SHARED_PREFS_DIR: String = "shared_prefs"
    private const val XML_SUFFIX: String = ".xml"

    fun buildDescriptorForAllPrefsFiles(
        context: Context
    ): List<SharedPreferencesDescriptor> {
        val descriptors = mutableListOf<SharedPreferencesDescriptor>()

        val dir = File(context.applicationInfo.dataDir, SHARED_PREFS_DIR)
        val list = dir.list { dir, name -> name.endsWith(XML_SUFFIX) }
        if (list != null) {
            for (each in list) {
                val prefName = each.substring(0, each.indexOf(".xml"))
                descriptors.add(SharedPreferencesDescriptor(prefName, MODE_PRIVATE))
            }
        }

        descriptors.add(
            SharedPreferencesDescriptor(getDefaultSharedPreferencesName(context), MODE_PRIVATE)
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