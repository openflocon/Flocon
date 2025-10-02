package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseByteSizeFormatted
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.ktor.http.Url

fun toUi(
    networkCall: FloconNetworkCallDomainModel,
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?
): NetworkItemViewState {
    return NetworkItemViewState(
        uuid = networkCall.callId,
        dateFormatted = networkCall.request.startTimeFormatted,
        timeFormatted = networkCall.response?.durationFormatted,
        requestSize = networkCall.request.byteSizeFormatted,
        responseSize = networkCall.responseByteSizeFormatted(),
        domain = networkCall.request.domainFormatted,
        type = toTypeUi(networkCall),
        method = getMethodUi(networkCall),
        status = getStatusUi(networkCall),
        isMocked = networkCall.request.isMocked,
        isFromOldAppInstance = deviceIdAndPackageName?.appInstance?.let { it != networkCall.appInstance } ?: false
    )
}
