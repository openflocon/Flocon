package io.github.openflocon.flocondesktop.features.network.mock.list.mapper

import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel

fun MockNetworkDomainModel.toLineUi(): MockNetworkLineUiModel = MockNetworkLineUiModel(
    id = id,
    isEnabled = isEnabled,
    urlPattern = expectation.urlPattern,
    method = toMockMethodUi(expectation.method),
)

fun toMockMethodUi(text: String): MockNetworkMethodUi = when (text.lowercase()) {
    "get" -> MockNetworkMethodUi.GET
    "post" -> MockNetworkMethodUi.POST
    "put" -> MockNetworkMethodUi.PUT
    "delete" -> MockNetworkMethodUi.DELETE
    "patch" -> MockNetworkMethodUi.PATCH
    else -> MockNetworkMethodUi.ALL
}
