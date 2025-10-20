package io.github.openflocon.flocon.plugins.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.SharedPreferencesDescriptor
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage

internal class FloconSharedPreferencesDataSourceImpl(
    private val context: Context,
) : FloconSharedPreferencesDataSource {

    private val mSharedPreferences: MutableMap<SharedPreferencesDescriptor, SharedPreferences> =
        mutableMapOf()
    private val mSharedPreferencesDescriptors: MutableMap<SharedPreferences, SharedPreferencesDescriptor> =
        mutableMapOf()

    private val onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(
                sharedPreferences: SharedPreferences,
                key: String?
            ) {
                val descriptor: SharedPreferencesDescriptor? =
                    mSharedPreferencesDescriptors[sharedPreferences]
                if (descriptor == null) {
                    return
                }
                // TODO sender?.send(
                // TODO     "sharedPreferencesChange",
                // TODO     Builder()
                // TODO         .put("preferences", descriptor.name)
                // TODO         .put("name", key)
                // TODO         .put("deleted", !sharedPreferences.contains(key))
                // TODO         .put("time", System.currentTimeMillis())
                // TODO         .put("value", sharedPreferences.getAll().get(key))
                // TODO         .build()
                // TODO )
            }
        }

    override fun getSharedPreferenceContent(sharedPreferencesName: String): List<SharedPreferenceRowDataModel> {
        val sharedPreferences = getSharedPreferencesFor(sharedPreferencesName) ?: return emptyList()
        return sharedPreferences.all.mapNotNull {
            when (val value = it.value) {
                is String -> SharedPreferenceRowDataModel(
                    key = it.key,
                    stringValue = value,
                )

                is Int -> SharedPreferenceRowDataModel(
                    key = it.key,
                    intValue = value
                )

                is Boolean -> SharedPreferenceRowDataModel(
                    key = it.key,
                    booleanValue = value
                )

                is Float -> SharedPreferenceRowDataModel(
                    key = it.key,
                    floatValue = value
                )

                is Long -> SharedPreferenceRowDataModel(
                    key = it.key,
                    longValue = value
                )

                is Set<*> -> SharedPreferenceRowDataModel(
                    key = it.key,
                    setStringValue = value
                        .map { it.toString() }
                        .toSet()
                )

                else -> null // Ne mappe pas les types non reconnus ou null
            }
        }
    }

    override fun getAllSharedPreferences(): List<SharedPreferencesDescriptor> {
        val foundPrefs: List<SharedPreferencesDescriptor> =
            SharedPreferencesFinder.buildDescriptorForAllPrefsFiles(context)
        for (descriptor in foundPrefs) {
            if (mSharedPreferences.containsKey(descriptor)) {
                // no op
            } else {
                descriptor.getSharedPreferences(context)?.let { sharedPref ->
                    sharedPref.registerOnSharedPreferenceChangeListener(
                        onSharedPreferenceChangeListener
                    )
                    mSharedPreferences.put(descriptor, sharedPref)
                    mSharedPreferencesDescriptors.put(sharedPref, descriptor)
                }
            }
        }
        return foundPrefs
    }

    private fun getSharedPreferencesFor(name: String?): SharedPreferences? {
        for (entry in mSharedPreferencesDescriptors.entries) {
            if (entry.value.name == name) {
                return entry.key
            }
        }
        return null
    }

    @Throws(Throwable::class)
    override fun setSharedPreferenceRowValue(
        sharedPreferencesName: String,
        preferenceName: String,
        message: ToDeviceEditSharedPreferenceValueMessage,
    ) {
        val sharedPreferences = getSharedPreferencesFor(sharedPreferencesName) ?: return
        val originalValue: Any? = sharedPreferences.all[preferenceName]
        val editor = sharedPreferences.edit()

        if (originalValue is Boolean && message.booleanValue != null) {
            editor.putBoolean(preferenceName, message.booleanValue)
        } else if (originalValue is Long && message.longValue != null) {
            editor.putLong(preferenceName, message.longValue)
        } else if (originalValue is Int && message.intValue != null) {
            editor.putInt(preferenceName, message.intValue)
        } else if (originalValue is Float && message.floatValue != null) {
            editor.putFloat(preferenceName, message.floatValue)
        } else if (originalValue is String && message.stringValue != null) {
            editor.putString(preferenceName, message.stringValue)
        } else {
            throw IllegalArgumentException("Method not supported: " + preferenceName)
        }

        editor.apply()
    }
}

internal fun SharedPreferencesDescriptor.getSharedPreferences(context: Context): SharedPreferences? {
    return context.getSharedPreferences(name, mode)
}

internal actual fun buildFloconSharedPreferencesDataSource(context: FloconContext): FloconSharedPreferencesDataSource {
    return FloconSharedPreferencesDataSourceImpl(context = context.appContext)
}