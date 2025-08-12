package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.EditableMockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.HeaderUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.SelectedMockUiModel
import java.util.UUID

fun toLineUi(mockDomain: MockNetworkDomainModel): MockNetworkLineUiModel {
    return MockNetworkLineUiModel(
        id = mockDomain.id,
        urlPattern = mockDomain.expectation.urlPattern,
        method = mockDomain.expectation.method,
    )
}

fun toDomain(uiModel: MockNetworkUiModel): MockNetworkDomainModel {
    return MockNetworkDomainModel(
        id = uiModel.id ?: UUID.randomUUID().toString(),
        expectation = MockNetworkDomainModel.Expectation(
            urlPattern = uiModel.expectation.urlPattern,
            method = uiModel.expectation.method.text,
        ),
        response = MockNetworkDomainModel.Response(
            httpCode = uiModel.response.httpCode,
            body = uiModel.response.body,
            mediaType = uiModel.response.mediaType,
            delay = uiModel.response.delay,
            headers = uiModel.response.headers,
        ),
    )
}

fun toMethodUi(text: String): MockNetworkMethodUi {
    return when (text.lowercase()) {
        "get" -> MockNetworkMethodUi.GET
        "post" -> MockNetworkMethodUi.POST
        "put" -> MockNetworkMethodUi.PUT
        "delete" -> MockNetworkMethodUi.DELETE
        "patch" -> MockNetworkMethodUi.PATCH
        else -> MockNetworkMethodUi.ALL
    }
}

fun toUi(domainModel: MockNetworkDomainModel): MockNetworkUiModel {
    return MockNetworkUiModel(
        id = domainModel.id,
        expectation = MockNetworkUiModel.Expectation(
            urlPattern = domainModel.expectation.urlPattern,
            method = toMethodUi(domainModel.expectation.method),
        ),
        response = MockNetworkUiModel.Response(
            httpCode = domainModel.response.httpCode,
            body = domainModel.response.body,
            mediaType = domainModel.response.mediaType,
            delay = domainModel.response.delay,
            headers = domainModel.response.headers,
        ),
    )
}

fun createEditable(initialMock: SelectedMockUiModel): EditableMockNetworkUiModel =
    when (initialMock) {
        is SelectedMockUiModel.Creation -> createEditable(null)
        is SelectedMockUiModel.Edition -> createEditable(initialMock.existing)
    }

fun createEditable(initialMock: MockNetworkUiModel?): EditableMockNetworkUiModel {
    return EditableMockNetworkUiModel(
        id = initialMock?.id,
        expectation = EditableMockNetworkUiModel.Expectation(
            urlPattern = initialMock?.expectation?.urlPattern,
            method = initialMock?.expectation?.method ?: MockNetworkMethodUi.GET,
        ),
        response = EditableMockNetworkUiModel.Response(
            httpCode = initialMock?.response?.httpCode ?: 200,
            body = initialMock?.response?.body,
            mediaType = initialMock?.response?.mediaType ?: "application/json",
            delay = initialMock?.response?.delay ?: 0,
            headers = initialMock?.response?.headers?.map {
                HeaderUiModel(
                    key = it.key,
                    value = it.value,
                )
            } ?: emptyList(),
        )
    )
}

fun editableToUi(editable: EditableMockNetworkUiModel): Either<Throwable, MockNetworkUiModel> {
    return try {
        MockNetworkUiModel(
            id = editable.id,
            expectation = MockNetworkUiModel.Expectation(
                urlPattern = editable.expectation.urlPattern!!,
                method = editable.expectation.method,
            ),
            response = MockNetworkUiModel.Response(
                httpCode = editable.response.httpCode,
                body = editable.response.body!!,
                mediaType = editable.response.mediaType,
                delay = editable.response.delay,
                headers = editable.response.headers.associate {
                    it.key to it.value
                }.filterNot { it.key.isEmpty() },
            )
        ).success()
    } catch (t: Throwable) {
        t.failure()
    }
}
