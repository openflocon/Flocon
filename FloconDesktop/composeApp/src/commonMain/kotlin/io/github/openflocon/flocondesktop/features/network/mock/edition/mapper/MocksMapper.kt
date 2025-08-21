package io.github.openflocon.flocondesktop.features.network.mock.edition.mapper

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.PossibleExceptionUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.EditableMockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.EditableMockNetworkUiModel.Response.*
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.EditableMockNetworkUiModel.ResponseType
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
    response = when(uiModel.response) {
        is MockNetworkUiModel.Response.Body -> MockNetworkDomainModel.Response.Body(
            httpCode = uiModel.response.httpCode,
            body = uiModel.response.body,
            mediaType = uiModel.response.mediaType,
            delay = uiModel.response.delay,
            headers = uiModel.response.headers,
        )
        is MockNetworkUiModel.Response.Exception -> MockNetworkDomainModel.Response.Exception(
            delay = uiModel.response.delay,
            classPath = uiModel.response.classPath,
        )
    }
)

fun toUi(domainModel: MockNetworkDomainModel): MockNetworkUiModel = MockNetworkUiModel(
    id = domainModel.id,
    expectation = MockNetworkUiModel.Expectation(
        urlPattern = domainModel.expectation.urlPattern,
        method = toMockMethodUi(domainModel.expectation.method),
    ),
    isEnabled = domainModel.isEnabled,
    response = when(val r = domainModel.response) {
        is MockNetworkDomainModel.Response.Body -> MockNetworkUiModel.Response.Body(
            httpCode = r.httpCode,
            body = r.body,
            mediaType = r.mediaType,
            delay = r.delay,
            headers = r.headers,
        )
        is MockNetworkDomainModel.Response.Exception -> MockNetworkUiModel.Response.Exception(
            delay = r.delay,
            classPath = r.classPath,
        )
    }
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
    delay = initialMock?.response?.delay ?: 0,
    exceptionResponse = EditableMockNetworkUiModel.Response.Exception(
        classPath = (initialMock?.response as? MockNetworkUiModel.Response.Exception)?.classPath ?: possibleExceptions.first().classPath,
    ),
    bodyResponse = editableBodyResponse(initialMock),
    responseType = when(initialMock?.response) {
        null,
        is MockNetworkUiModel.Response.Body -> EditableMockNetworkUiModel.ResponseType.BODY
        is MockNetworkUiModel.Response.Exception -> EditableMockNetworkUiModel.ResponseType.EXCEPTION
    },
)

private fun editableBodyResponse(initialMock: MockNetworkUiModel?): EditableMockNetworkUiModel.Response.Body {
    val bodyResponse = (initialMock?.response as? MockNetworkUiModel.Response.Body)
    return EditableMockNetworkUiModel.Response.Body(
        httpCode = bodyResponse?.httpCode ?: 200,
        body = bodyResponse?.body ?: "",
        mediaType = bodyResponse?.mediaType ?: "application/json",

        headers = bodyResponse?.headers?.map {
            HeaderUiModel(
                key = it.key,
                value = it.value,
            )
        } ?: emptyList(),
    )
}

fun editableToUi(editable: EditableMockNetworkUiModel): Either<Throwable, MockNetworkUiModel> = try {
    MockNetworkUiModel(
        id = editable.id,
        expectation = MockNetworkUiModel.Expectation(
            urlPattern = editable.expectation.urlPattern!!,
            method = editable.expectation.method,
        ),
        isEnabled = editable.isEnabled,
        response = when(editable.responseType) {
            ResponseType.BODY -> MockNetworkUiModel.Response.Body(
                httpCode = editable.bodyResponse.httpCode,
                body = editable.bodyResponse.body,
                mediaType = editable.bodyResponse.mediaType,
                delay = editable.delay,
                headers = editable.bodyResponse.headers.associate {
                    it.key to it.value
                }.filterNot { it.key.isEmpty() },
            )
            ResponseType.EXCEPTION -> MockNetworkUiModel.Response.Exception(
                delay = editable.delay,
                classPath = editable.exceptionResponse.classPath,
            )
        }
    ).success()
} catch (t: Throwable) {
    t.failure()
}
