package io.github.openflocon.data.core.webview.repository

import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import io.github.openflocon.data.core.webview.datasource.WebViewLocalDataSource
import io.github.openflocon.data.core.webview.datasource.WebViewRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import io.github.openflocon.domain.webview.repository.WebViewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class WebViewRepositoryImpl(
    private val localDataSource: WebViewLocalDataSource,
    private val remoteDataSource: WebViewRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : WebViewRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.WebView.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        val events = when (message.method) {
            Protocol.FromDevice.WebView.Method.AddConsoleLogs -> remoteDataSource.getConsoleLogs(message)
            Protocol.FromDevice.WebView.Method.AddPageEvents -> remoteDataSource.getPageEvents(message)
            Protocol.FromDevice.WebView.Method.AddBridgeCalls -> remoteDataSource.getBridgeCalls(message)
            else -> return
        }
        if (events.isEmpty()) return
        try {
            withContext(dispatcherProvider.data) {
                localDataSource.insertAll(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    events = events,
                )
            }
        } catch (t: Throwable) {
            Logger.e("error on message received / webView", t)
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // No-op
    }

    override fun observeEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        filter: String?,
    ): Flow<PagingData<WebViewEventDomainModel>> = localDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        kind = kind,
        filter = filter,
    ).flowOn(dispatcherProvider.data)

    override suspend fun searchEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        kind: WebViewEventKind?,
        sinceTimestamp: Long?,
        text: String?,
        newestFirst: Boolean,
        limit: Int,
    ): List<WebViewEventDomainModel> = withContext(dispatcherProvider.data) {
        localDataSource.search(
            deviceIdAndPackageName = deviceIdAndPackageName,
            kind = kind,
            sinceTimestamp = sinceTimestamp,
            text = text,
            newestFirst = newestFirst,
            limit = limit,
        )
    }

    override suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            localDataSource.clearAll(deviceIdAndPackageName)
        }
    }
}
