package com.flocon.data.remote.network.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

// be sure this keep aligned with the ui mapper

internal fun failureStatus() = "Exception"

internal fun extractStatus(specificInfos: FloconNetworkCallDomainModel.Response.Success.SpecificInfos): String =
    when (val s = specificInfos) {
        is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> toGraphQlNetworkStatus(isSuccess = s.isSuccess)
        is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> toNetworkStatus(s.httpCode)
        is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> toGrpcNetworkStatus(specificInfos)
    }

private fun toNetworkStatus(code: Int): String = code.toString()

private fun toGraphQlNetworkStatus(isSuccess: Boolean): String =
    if (isSuccess) "Success" else "Error"

private fun toGrpcNetworkStatus(
    specificInfos: FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc
): String {
    return specificInfos.grpcStatus
}
