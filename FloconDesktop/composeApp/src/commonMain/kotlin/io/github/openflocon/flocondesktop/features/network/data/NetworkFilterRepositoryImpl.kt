package io.github.openflocon.flocondesktop.features.network.data

import com.flocon.library.domain.models.DeviceId
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkFilterLocalDataSource
import com.flocon.library.domain.models.NetworkTextFilterColumns
import com.flocon.library.domain.models.TextFilterStateDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkFilterRepositoryImpl(
    private val networkFilterLocalDataSource: NetworkFilterLocalDataSource,
) : NetworkFilterRepository {
    override suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel? = networkFilterLocalDataSource.get(
        deviceId = deviceId,
        column = column,
    )

    override fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> = networkFilterLocalDataSource.observe(
        deviceId = deviceId,
    ).distinctUntilChanged()

    override suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) = networkFilterLocalDataSource.update(
        deviceId = deviceId,
        column = column,
        newValue = newValue,
    )
}
