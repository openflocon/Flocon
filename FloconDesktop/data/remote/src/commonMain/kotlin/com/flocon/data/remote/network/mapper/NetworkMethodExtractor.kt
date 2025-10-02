package com.flocon.data.remote.network.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

internal fun extractMethod(
    requestMethod: String,
    specificInfos: FloconNetworkCallDomainModel.Request.SpecificInfos,
): String = when (specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> specificInfos.operationType.lowercase()
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> toHttpMethodUi(requestMethod)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> "grpc"
}

private fun toHttpMethodUi(httpMethod: String) = httpMethod.lowercase()
