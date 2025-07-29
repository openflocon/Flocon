package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceEditSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
    val key: String,
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: Set<String>? = null,
) {
    companion object {
        fun fromJson(jsonString: String): ToDeviceEditSharedPreferenceValueMessage? {
            return try {
                val jsonObject = JSONObject(jsonString)

                val stringValue = jsonObject.optString("stringValue")
                val intValue =
                    if (jsonObject.has("intValue")) jsonObject.getInt("intValue") else null
                val floatValue =
                    if (jsonObject.has("floatValue")) jsonObject.getDouble("floatValue")
                        .toFloat() else null
                val booleanValue =
                    if (jsonObject.has("booleanValue")) jsonObject.getBoolean("booleanValue") else null
                val longValue =
                    if (jsonObject.has("longValue")) jsonObject.getLong("longValue") else null

                val setStringValue = if (jsonObject.has("setStringValue")) {
                    jsonObject.getJSONArray("setStringValue").let { array ->
                        (0 until array.length()).mapNotNull {
                            array.optString(it, null)
                        }.toSet()
                    }
                } else null

                ToDeviceEditSharedPreferenceValueMessage(
                    key = jsonObject.getString("key"),
                    requestId = jsonObject.getString("requestId"),
                    sharedPreferenceName = jsonObject.getString("sharedPreferenceName"),
                    stringValue = stringValue,
                    intValue = intValue,
                    floatValue = floatValue,
                    booleanValue = booleanValue,
                    longValue = longValue,
                    setStringValue = setStringValue
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}
