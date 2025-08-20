package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.model.BadQualityConfig
import org.json.JSONArray
import org.json.JSONObject

fun toJsonObject(config: BadQualityConfig): JSONObject {
    val jsonObject = JSONObject()

    // Sérialisation de la configuration de latence
    val latencyObject = JSONObject()
    latencyObject.put("latencyTriggerProbability", config.latency.latencyTriggerProbability)
    latencyObject.put("minLatencyMs", config.latency.minLatencyMs)
    latencyObject.put("maxLatencyMs", config.latency.maxLatencyMs)
    jsonObject.put("latency", latencyObject)

    // Sérialisation de la probabilité d'erreur
    jsonObject.put("errorProbability", config.errorProbability)

    // Sérialisation de la liste des erreurs
    val errorsArray = JSONArray()
    config.errors.forEach { error ->
        val errorObject = JSONObject()
        errorObject.put("weight", error.weight)
        when(val t = error.type) {
            is BadQualityConfig.Error.Type.Body -> {
                errorObject.put("errorCode", t.errorCode)
                errorObject.put("errorBody", t.errorBody)
                errorObject.put("errorContentType", t.errorContentType)
            }
            is BadQualityConfig.Error.Type.ErrorThrow -> {
                errorObject.put("errorException", t.classPath)
            }
        }
        errorsArray.put(errorObject)
    }
    jsonObject.put("errors", errorsArray)

    return jsonObject
}

fun parseBadQualityConfig(jsonString: String): BadQualityConfig? {
    return try {
        val jsonObject = JSONObject(jsonString)

        // Parsing de la configuration de latence
        val latencyObject = jsonObject.getJSONObject("latency")
        val latencyConfig = BadQualityConfig.LatencyConfig(
            latencyTriggerProbability = latencyObject.getDouble("latencyTriggerProbability")
                .toFloat(),
            minLatencyMs = latencyObject.getLong("minLatencyMs"),
            maxLatencyMs = latencyObject.getLong("maxLatencyMs")
        )

        // Parsing de la probabilité d'erreur
        val errorProbability = jsonObject.getDouble("errorProbability")

        // Parsing de la liste des erreurs
        val errorsArray = jsonObject.getJSONArray("errors")
        val errorsList = mutableListOf<BadQualityConfig.Error>()
        for (i in 0 until errorsArray.length()) {
            val errorObject = errorsArray.getJSONObject(i)
            try {
                val errorException = errorObject.optString("errorException")
                val errorCode = errorObject.optInt("errorCode")
                val errorBody = errorObject.optString("errorBody")
                val errorContentType = errorObject.optString("errorContentType")
                val error = BadQualityConfig.Error(
                    weight = errorObject.getDouble("weight").toFloat(),
                    type = if (errorException.isNotEmpty()) {
                        BadQualityConfig.Error.Type.ErrorThrow(errorException)
                    } else {
                        BadQualityConfig.Error.Type.Body(
                            errorCode = errorCode,
                            errorBody = errorBody,
                            errorContentType = errorContentType
                        )
                    }
                )
                errorsList.add(error)
            } catch (t: Throwable) {
                FloconLogger.logError(t.message ?: "bad connection network parsing issue", t)
            }
        }

        BadQualityConfig(
            latency = latencyConfig,
            errorProbability = errorProbability,
            errors = errorsList
        )
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "bad connection network parsing issue", t)
        null
    }
}