package com.flocon.data.remote.webview.datasource

import co.touchlab.kermit.Logger
import com.flocon.data.remote.webview.mapper.toDomain
import com.flocon.data.remote.webview.model.WebViewBridgeCallDataModel
import com.flocon.data.remote.webview.model.WebViewConsoleLogDataModel
import com.flocon.data.remote.webview.model.WebViewPageEventDataModel
import io.github.openflocon.data.core.webview.datasource.WebViewRemoteDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import kotlinx.serialization.json.Json

internal class WebViewRemoteDataSourceImpl(
    private val json: Json,
) : WebViewRemoteDataSource {

    override fun getConsoleLogs(message: FloconIncomingMessageDomainModel): List<WebViewEventDomainModel> = decode<WebViewConsoleLogDataModel>(message, "consoleLogs").map { it.toDomain() }

    override fun getPageEvents(message: FloconIncomingMessageDomainModel): List<WebViewEventDomainModel> = decode<WebViewPageEventDataModel>(message, "pageEvents").map { it.toDomain() }

    override fun getBridgeCalls(message: FloconIncomingMessageDomainModel): List<WebViewEventDomainModel> = decode<WebViewBridgeCallDataModel>(message, "bridgeCalls").map { it.toDomain() }

    private inline fun <reified T> decode(message: FloconIncomingMessageDomainModel, what: String): List<T> = try {
        json.decodeFromString<List<T>>(message.body)
    } catch (t: Throwable) {
        Logger.e("error on decode / webView $what", t)
        emptyList()
    }
}
