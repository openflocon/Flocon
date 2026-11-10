package io.github.openflocon.data.local.webview.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.github.openflocon.data.core.webview.datasource.WebViewLocalDataSource
import io.github.openflocon.data.local.webview.dao.WebViewEventDao
import io.github.openflocon.data.local.webview.mapper.toDomain
import io.github.openflocon.data.local.webview.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class WebViewLocalDataSourceRoom(
    private val webViewEventDao: WebViewEventDao,
) : WebViewLocalDataSource {

    override suspend fun insertAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        events: List<WebViewEventDomainModel>,
    ) {
        webViewEventDao.insertAll(events.map { it.toEntity(deviceIdAndPackageName) })
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        filter: String?,
    ): Flow<PagingData<WebViewEventDomainModel>> = Pager(
        config = PagingConfig(pageSize = 50),
        pagingSourceFactory = {
            webViewEventDao.observe(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                kind = kind?.name,
                filter = filter,
            )
        }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain() }
    }

    override suspend fun search(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        sinceTimestamp: Long?,
        text: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<WebViewEventDomainModel> = webViewEventDao.search(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        kind = kind?.name,
        sinceTimestamp = sinceTimestamp,
        filter = text,
        newestFirst = newestFirst,
        limit = limit,
    ).map { it.toDomain() }

    override suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        webViewEventDao.clearAll(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }
}
