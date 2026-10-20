package io.github.openflocon.data.core.timeline.repository

import androidx.paging.PagingData
import io.github.openflocon.data.core.timeline.datasources.TimelineLocalDataSource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class TimelineRepositoryImpl(
    private val localDataSource: TimelineLocalDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : TimelineRepository {

    override fun observeTimeline(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: TimelineFilterDomainModel,
    ): Flow<PagingData<TimelineEventDomainModel>> = localDataSource.observeTimeline(
        deviceIdAndPackageName = deviceIdAndPackageName,
        filter = filter,
    ).flowOn(dispatcherProvider.data)
}
