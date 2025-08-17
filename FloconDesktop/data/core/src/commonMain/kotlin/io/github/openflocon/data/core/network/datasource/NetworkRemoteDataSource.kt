package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallIdDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkResponseOnlyDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel

interface NetworkRemoteDataSource {

    suspend fun setupMocks(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        mocks: List<MockNetworkDomainModel>,
    )

    suspend fun setupBadNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel?
    )

    fun getRequestData(message: FloconIncomingMessageDomainModel): FloconNetworkCallDomainModel?

    fun getCallId(message: FloconIncomingMessageDomainModel): FloconNetworkCallIdDomainModel?

    fun getResponseData(message: FloconIncomingMessageDomainModel): FloconNetworkResponseOnlyDomainModel?
}
