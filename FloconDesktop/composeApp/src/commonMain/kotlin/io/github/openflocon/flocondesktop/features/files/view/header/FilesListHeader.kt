package io.github.openflocon.flocondesktop.features.files.view.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.date
import flocondesktop.composeapp.generated.resources.name
import flocondesktop.composeapp.generated.resources.size
import io.github.openflocon.flocondesktop.features.files.model.FileColumnUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesHeaderStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.isSorted
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FilesListHeader(
    modifier: Modifier = Modifier,
    state: FilesHeaderStateUiModel,
    isSelecting: Boolean,
    selectingAll: Boolean,
    onSelectAll: (selectingAll: Boolean) -> Unit,
    clickOnSort: (FileColumnUiModel, SortedByUiModel) -> Unit,
) {
    Row(
        modifier = modifier.background(FloconTheme.colorPalette.secondary)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
            if(isSelecting) {
                FloconCheckbox(
                    checked = selectingAll,
                    onCheckedChange = {
                        onSelectAll(!selectingAll)
                    }
                )
            }
        }

        FilesListHeaderButton(
            stringResource(Res.string.name),
            sortedBy = state.isSorted(FileColumnUiModel.Name),
            clickOnSort = {
                clickOnSort(FileColumnUiModel.Name, it)
            },
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )

        FilesListHeaderButton(
            title = stringResource(Res.string.date),
            textAlign = TextAlign.Center,
            sortedBy = state.isSorted(FileColumnUiModel.Date),
            clickOnSort = {
                clickOnSort(FileColumnUiModel.Date, it)
            },
            modifier = Modifier.width(150.dp),
        )

        FilesListHeaderButton(
            title = stringResource(Res.string.size),
            subtitle = state.totalSizeFormatted,
            textAlign = TextAlign.End,
            modifier = Modifier.width(100.dp),
            sortedBy = state.isSorted(FileColumnUiModel.Size),
            clickOnSort = {
                clickOnSort(FileColumnUiModel.Size, it)
            },
        )

        Spacer(modifier = Modifier.width(24.dp))
    }
}
