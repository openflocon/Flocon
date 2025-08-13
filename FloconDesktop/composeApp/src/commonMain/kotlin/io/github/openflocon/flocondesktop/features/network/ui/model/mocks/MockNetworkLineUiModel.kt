package io.github.openflocon.flocondesktop.features.network.ui.model.mocks

import androidx.compose.runtime.Immutable

@Immutable
data class MockNetworkLineUiModel(
    val id: String,
    val isEnabled: Boolean,
    val urlPattern: String,
    val method: MockNetworkMethodUi,
)

fun previewMockNetworkLineUiModel(method: MockNetworkMethodUi = MockNetworkMethodUi.GET) = MockNetworkLineUiModel(
    id = "1",
    isEnabled = true,
    urlPattern = "http://.*youtube.*v=.*",
    method = method,
)
