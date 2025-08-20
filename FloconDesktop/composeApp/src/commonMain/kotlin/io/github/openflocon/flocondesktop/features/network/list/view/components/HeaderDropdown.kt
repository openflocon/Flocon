@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu

@Composable
fun HeaderDropdown(
    label: String,
    filtered: Boolean,
    sortedBy: SortedByUiModel,
    onClickSort: (SortedByUiModel.Enabled) -> Unit,
    modifier: Modifier = Modifier,
    labelAlignment: Alignment = Alignment.Center,
    dropdownMenu: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        HeaderLabelItem(
            modifier = Modifier
                .fillMaxWidth(),
            text = label,
            filtered = filtered,
            sortedBy = sortedBy,
            clickOnSort = onClickSort,
            clickOnFilter = { expanded = true },
            labelAlignment = labelAlignment
        )
        FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = dropdownMenu
        )
    }
}
