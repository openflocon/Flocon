package io.github.openflocon.flocondesktop.features.network.model.mocks

import androidx.compose.runtime.Immutable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Immutable
data class MockEditionWindowUiModel(
    val selectedMockUiModel: SelectedMockUiModel,
    val windowInstanceId: String = Uuid.random().toString(),
)
