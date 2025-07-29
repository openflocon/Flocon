package com.github.openflocon.flocon.plugins.sharedprefs.model

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.forEach

data class SharedPreferencesDescriptor(
    val name: String,
    val mode: Int,
) {
    fun getSharedPreferences(context: Context): SharedPreferences? {
        return context.getSharedPreferences(name, mode)
    }
}

fun listSharedPreferencesDescriptorToJson(items: List<SharedPreferencesDescriptor>) : JSONArray {
    val array = JSONArray()
    items.forEach {
        val jsonObject = JSONObject().apply {
            put("name", it.name)
        }
        array.put(jsonObject)
    }
    return array
}