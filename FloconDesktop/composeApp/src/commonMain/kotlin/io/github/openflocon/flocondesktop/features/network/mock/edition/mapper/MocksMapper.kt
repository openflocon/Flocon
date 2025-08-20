package io.github.openflocon.flocondesktop.features.network.mock.edition.mapper

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.EditableMockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.HeaderUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.SelectedMockUiModel
import io.github.openflocon.flocondesktop.features.network.mock.list.mapper.toMockMethodUi
import java.util.UUID

fun toDomain(uiModel: MockNetworkUiModel): MockNetworkDomainModel = MockNetworkDomainModel(
    id = uiModel.id ?: UUID.randomUUID().toString(),
    isEnabled = uiModel.isEnabled,
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

fun toUi(domainModel: MockNetworkDomainModel): MockNetworkUiModel = MockNetworkUiModel(
    id = domainModel.id,
    expectation = MockNetworkUiModel.Expectation(
        urlPattern = domainModel.expectation.urlPattern,
        method = toMockMethodUi(domainModel.expectation.method),
    ),
    isEnabled = domainModel.isEnabled,
    response = MockNetworkUiModel.Response(
        httpCode = domainModel.response.httpCode,
        body = domainModel.response.body,
        mediaType = domainModel.response.mediaType,
        delay = domainModel.response.delay,
        headers = domainModel.response.headers,
    ),
)

fun createEditable(initialMock: SelectedMockUiModel): EditableMockNetworkUiModel = when (initialMock) {
    is SelectedMockUiModel.Creation -> createEditable(null)
    is SelectedMockUiModel.Edition -> createEditable(initialMock.existing)
}

fun createEditable(initialMock: MockNetworkUiModel?): EditableMockNetworkUiModel = EditableMockNetworkUiModel(
    id = initialMock?.id,
    isEnabled = initialMock?.isEnabled ?: true, // true by default
    expectation = EditableMockNetworkUiModel.Expectation(
        urlPattern = initialMock?.expectation?.urlPattern,
        method = initialMock?.expectation?.method ?: MockNetworkMethodUi.GET,
    ),
    response = EditableMockNetworkUiModel.Response(
        httpCode = initialMock?.response?.httpCode ?: 200,
        body = initialMock?.response?.body ?: "",
        mediaType = initialMock?.response?.mediaType ?: "application/json",
        delay = initialMock?.response?.delay ?: 0,
        headers = initialMock?.response?.headers?.map {
            HeaderUiModel(
                key = it.key,
                value = it.value,
            )
        } ?: emptyList(),
    ),
)

fun editableToUi(editable: EditableMockNetworkUiModel): Either<Throwable, MockNetworkUiModel> = try {
    MockNetworkUiModel(
        id = editable.id,
        expectation = MockNetworkUiModel.Expectation(
            urlPattern = editable.expectation.urlPattern!!,
            method = editable.expectation.method,
        ),
        isEnabled = editable.isEnabled,
        response = MockNetworkUiModel.Response(
            httpCode = editable.response.httpCode,
            body = editable.response.body!!,
            mediaType = editable.response.mediaType,
            delay = editable.response.delay,
            headers = editable.response.headers.associate {
                it.key to it.value
            }.filterNot { it.key.isEmpty() },
        ),
    ).success()
} catch (t: Throwable) {
    t.failure()
}
