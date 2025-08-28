package io.github.openflocon.data.core.network.repository

import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkFilterRepositoryImpl(
    private val networkFilterLocalDataSource: NetworkFilterLocalDataSource,
) : NetworkFilterRepository {
    override suspend fun get(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel? = networkFilterLocalDataSource.get(
        deviceAndApp = deviceAndApp,
        column = column,
    )

    override fun observe(deviceAndApp: DeviceIdAndPackageNameDomainModel): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> = networkFilterLocalDataSource.observe(
        deviceAndApp = deviceAndApp,
    ).distinctUntilChanged()

    override suspend fun update(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) = networkFilterLocalDataSource.update(
        deviceAndApp = deviceAndApp,
        column = column,
        newValue = newValue,
    )
}
