package io.github.openflocon.data.core.webview.datasource

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import kotlinx.coroutines.flow.Flow

interface WebViewLocalDataSource {
    suspend fun insertAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        events: List<WebViewEventDomainModel>,
    )

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        filter: String?,
    ): Flow<PagingData<WebViewEventDomainModel>>

    suspend fun search(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        sinceTimestamp: Long?,
        text: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<WebViewEventDomainModel>

    suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}
