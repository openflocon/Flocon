package com.flocon.data.remote.network.mapper

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

fun extractQueryFormatted(
    requestUrl: String,
    requestMethod: String,
    specificInfos: FloconNetworkCallDomainModel.Request.SpecificInfos,
): String = when (val s = specificInfos) {
    is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> s.query
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> extractPath(requestUrl)
    is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> requestMethod
}
