package io.github.openflocon.flocondesktop.features.network.mock.list.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi

@Immutable
data class MockNetworkLineUiModel(
    val id: String,
    val isEnabled: Boolean,
    val urlPattern: String,
    val isShared: Boolean,
    val method: MockNetworkMethodUi,
    val displayName: String,
)

fun previewMockNetworkLineUiModel(method: MockNetworkMethodUi = MockNetworkMethodUi.GET) = MockNetworkLineUiModel(
    id = "1",
    isEnabled = true,
    urlPattern = "http://.*youtube.*v=.*",
    method = method,
    isShared = false,
    displayName = "Mock for YouTube video",
)
