package com.flocon.data.remote.messages.mapper

import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

internal fun FloconIncomingMessageDataModel.toDomain() = FloconIncomingMessageDomainModel(
    deviceName = deviceName,
    deviceId = deviceId,
    appName = appName,
    appPackageName = appPackageName,
    method = method,
    body = body,
    plugin = plugin,
)
