package io.github.openflocon.flocon.network.core.model

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
            return latencyTriggerProbability > 0f && (latencyTriggerProbability == 1f || Random.nextDouble() < latencyTriggerProbability)
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
                        io.github.openflocon.flocon.utils.createThrowableFromClassName(classPath)
                    } catch (t: Throwable) {
                        FloconLogger.logError("BadQualityConfig error, className not found", t)
                        null
                    }
                }
            }
        }
    }

    fun shouldFail(): Boolean {
        return errorProbability > 0 && Random.nextDouble() < errorProbability
    }

    fun selectRandomError(): BadQualityConfig.Error? {
        if (errors.isEmpty()) {
            return null
        }

        val totalWeight = errors.sumOf { it.weight.toDouble() }
        var randomNumber = Random.nextDouble(0.0, totalWeight)

        for (error in errors) {
            randomNumber -= error.weight.toDouble()
            if (randomNumber <= 0) {
                return error
            }
        }

        return errors.first()
    }
}
