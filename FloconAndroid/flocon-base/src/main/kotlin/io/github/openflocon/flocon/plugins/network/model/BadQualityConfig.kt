package io.github.openflocon.flocon.plugins.network.model

import io.github.openflocon.flocon.FloconLogger
import kotlin.random.Random

data class BadQualityConfig(
    val latency: LatencyConfig,
    val errorProbability: Double, // chance of triggering an error
    val errors: List<Error>, // list of errors
) {
    class LatencyConfig(
        val latencyTriggerProbability: Float,
        val minLatencyMs: Long,
        val maxLatencyMs: Long,
    ) {
        fun shouldSimulateLatency(): Boolean {
            return latencyTriggerProbability > 0f && (latencyTriggerProbability == 1f || Math.random() < latencyTriggerProbability)
        }
        fun getRandomLatency() : Long {
            return Random.nextLong(
                minLatencyMs,
                maxLatencyMs + 1
            )
        }
    }
    class Error(
        val weight: Float, // increase the probability of being triggered vs all others errors
        val type: Type,
    ) {
        sealed interface Type {
            data class Body(
                val errorCode: Int,
                val errorBody: String,
                val errorContentType: String, // "application/json"
            ) : Type
            data class ErrorThrow(
                val classPath: String,
            ) : Type {
                fun generate() : Throwable? {
                    return try {
                        val errorClass = Class.forName(classPath)
                        errorClass.newInstance() as? Throwable
                    } catch (t: Throwable) {
                        FloconLogger.logError("BadQualityConfig error, className not found", t)
                        null
                    }
                }
            }
        }
    }

    fun shouldFail(): Boolean {
        return errorProbability > 0 && Math.random() < errorProbability
    }

    fun selectRandomError(): BadQualityConfig.Error? {
        if (errors.isEmpty()) {
            return null
        }

        // Calculer la somme totale des poids
        val totalWeight = errors.sumOf { it.weight.toDouble() }

        // Générer un nombre aléatoire entre 0 et la somme totale des poids
        var randomNumber = Random.nextDouble(0.0, totalWeight)

        // Parcourir la liste pour trouver l'erreur sélectionnée
        for (error in errors) {
            randomNumber -= error.weight.toDouble()
            if (randomNumber <= 0) {
                return error
            }
        }

        // Cas de secours (ne devrait pas arriver si les poids sont positifs)
        return errors.first()
    }
}
