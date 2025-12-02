package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseByteSizeFormatted
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState

fun FloconNetworkCallDomainModel.toUi(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?
): NetworkItemViewState = NetworkItemViewState(
    uuid = callId,
    dateFormatted = request.startTimeFormatted,
    timeFormatted = response?.durationFormatted,
    requestSize = request.byteSizeFormatted,
    responseSize = responseByteSizeFormatted(),
    domain = request.domainFormatted,
    type = toTypeUi(this),
    method = getMethodUi(this),
    status = getStatusUi(this),
    isMocked = request.isMocked,
    isReplayed = isReplayed,
    isFromOldAppInstance = deviceIdAndPackageName?.appInstance?.let { it != appInstance } ?: false
)
