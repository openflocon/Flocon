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
        errorObject.put("errorCode", error.errorCode)
        errorObject.put("errorBody", error.errorBody)
        errorObject.put("errorContentType", error.errorContentType)
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
            val error = BadQualityConfig.Error(
                weight = errorObject.getDouble("weight").toFloat(),
                errorCode = errorObject.getInt("errorCode"),
                errorBody = errorObject.getString("errorBody"),
                errorContentType = errorObject.getString("errorContentType")
            )
            errorsList.add(error)
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