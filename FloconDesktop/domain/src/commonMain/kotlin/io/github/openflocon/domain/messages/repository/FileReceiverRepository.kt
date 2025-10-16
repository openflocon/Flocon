package io.github.openflocon.domain.messages.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel

interface FileReceiverRepository {
    suspend fun onFileReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        receivedFile: FloconReceivedFileDomainModel,
    )
}
