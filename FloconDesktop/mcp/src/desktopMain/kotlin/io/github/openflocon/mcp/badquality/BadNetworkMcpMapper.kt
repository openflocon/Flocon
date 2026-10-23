package io.github.openflocon.mcp.badquality

import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel

internal const val ERROR_TYPE_BODY = "body"
internal const val ERROR_TYPE_EXCEPTION = "exception"

internal fun BadQualityConfigDomainModel.toMcpModel() = BadNetworkConfigMcpModel(
    configId = id,
    name = name,
    isEnabled = isEnabled,
    latency = BadNetworkLatencyMcpModel(
        triggerProbability = latency.triggerProbability,
        minLatencyMs = latency.minLatencyMs,
        maxLatencyMs = latency.maxLatencyMs,
    ),
    errorProbability = errorProbability,
    errors = errors.map { error ->
        when (val type = error.type) {
            is BadQualityConfigDomainModel.Error.Type.Body -> BadNetworkErrorMcpModel(
                type = ERROR_TYPE_BODY,
                weight = error.weight,
                httpCode = type.httpCode,
                contentType = type.contentType,
                body = type.body,
            )

            is BadQualityConfigDomainModel.Error.Type.Exception -> BadNetworkErrorMcpModel(
                type = ERROR_TYPE_EXCEPTION,
                weight = error.weight,
                exceptionClassPath = type.classPath,
            )
        }
    },
)
