package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.NetworkEditionWindow
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.mock.list.model.previewMockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconActionButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.floconMonoStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_create
import flocondesktop.composeapp.generated.resources.general_export
import flocondesktop.composeapp.generated.resources.general_import
import flocondesktop.composeapp.generated.resources.mocks_subtitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkMocksWindow(
    fromNetworkCallId: String?
) {
    val viewModel: NetworkMocksViewModel = koinViewModel()
    LaunchedEffect(viewModel, fromNetworkCallId) {
        viewModel.initWith(fromNetworkCallId)
    }
    val mocks by viewModel.items.collectAsStateWithLifecycle()
    val editionWindow by viewModel.editionWindow.collectAsStateWithLifecycle()

    NetworkMocksContent(
        mocks = mocks,
        modifier = Modifier.fillMaxWidth(),
        onAction = viewModel::onAction,
    )

    editionWindow?.let {
        NetworkEditionWindow(
            instanceId = it.windowInstanceId,
            state = it.selectedMockUiModel,
            onCloseRequest = viewModel::cancelMockCreation,
            onCancel = viewModel::cancelMockCreation,
            onSave = viewModel::addMock,
        )
    }
}

sealed interface NetworkMockAction {
    data class OnItemClicked(val id: String) : NetworkMockAction
    data class OnDeleteClicked(val id: String) : NetworkMockAction
    data class ChangeIsEnabled(val id: String, val enabled: Boolean) : NetworkMockAction
    data class ChangeIsShared(val id: String, val isShared: Boolean) : NetworkMockAction
    data object OnImportClicked : NetworkMockAction
    data object OnExportClicked : NetworkMockAction
    data object OnAddItemClicked : NetworkMockAction
}

@Composable
internal fun NetworkMocksContent(
    mocks: ImmutableList<MockNetworkLineUiModel>,
    onAction: (NetworkMockAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            ) {
                Text(
                    text = mocks.size.toString(),
                    style = floconMonoStyle(small = true),
                    color = colors.inkMuted,
                    modifier = Modifier
                        .background(colors.bg300, CircleShape)
                        .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
                )
                Text(
                    text = stringResource(Res.string.mocks_subtitle),
                    style = FloconTheme.typography.bodyMedium,
                    color = colors.inkMuted,
                )
            }
            FloconActionButton(
                onClick = { onAction(NetworkMockAction.OnAddItemClicked) },
                primary = true,
            ) {
                Text(stringResource(Res.string.general_create), fontWeight = FontWeight.SemiBold)
            }
            FloconOverflow {
                FloconDropdownMenuItem(
                    text = stringResource(Res.string.general_import),
                    leadingIcon = Icons.Outlined.FileDownload,
                    onClick = { onAction(NetworkMockAction.OnImportClicked) }
                )
                FloconDropdownMenuItem(
                    text = stringResource(Res.string.general_export),
                    leadingIcon = Icons.Outlined.FileUpload,
                    onClick = { onAction(NetworkMockAction.OnExportClicked) }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bg100, FloconTheme.shapes.large)
                .border(1.dp, colors.line, FloconTheme.shapes.large)
        ) {
            MocksHeaderView(Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                items(
                    items = mocks,
                    key = MockNetworkLineUiModel::id
                ) {
                    MockLineView(
                        item = it,
                        onClicked = { id -> onAction(NetworkMockAction.OnItemClicked(id)) },
                        onDeleteClicked = { id -> onAction(NetworkMockAction.OnDeleteClicked(id)) },
                        changeIsEnabled = { id, enabled ->
                            onAction(
                                NetworkMockAction.ChangeIsEnabled(
                                    id = id,
                                    enabled = enabled
                                )
                            )
                        },
                        changeIsShared = { id, isShared ->
                            onAction(
                                NetworkMockAction.ChangeIsShared(
                                    id = id,
                                    isShared = isShared
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun NetworkMocksContentPreview() {
    FloconTheme {
        NetworkMocksContent(
            mocks = List(10) {
                previewMockNetworkLineUiModel()
            }.toImmutableList(),
            onAction = {},
        )
    }
}
