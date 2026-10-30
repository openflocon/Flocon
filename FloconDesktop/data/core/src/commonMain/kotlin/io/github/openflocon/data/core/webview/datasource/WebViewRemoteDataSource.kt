package io.github.openflocon.data.core.webview.datasource

import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel

interface WebViewRemoteDataSource {
    fun getConsoleLogs(message: FloconIncomingMessageDomainModel): List<WebViewEventDomainModel>
    fun getPageEvents(message: FloconIncomingMessageDomainModel): List<WebViewEventDomainModel>
    fun getBridgeCalls(message: FloconIncomingMessageDomainModel): List<WebViewEventDomainModel>
}
