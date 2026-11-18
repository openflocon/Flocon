package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import kotlinx.coroutines.flow.Flow

interface NetworkFilterLocalDataSource {
    suspend fun get(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel?

    fun observe(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
    ): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>>

    suspend fun update(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    )
}
