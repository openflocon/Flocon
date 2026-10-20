package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardTextFieldView(
    rowItem: DashboardContainerViewState.RowItem.TextField,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit = {},
    showSubmitButton: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        Text(
            text = rowItem.label,
            modifier = Modifier.padding(start = FloconTheme.spacing.extraSmall),
            color = FloconTheme.colorPalette.onPrimary,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Thin,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconTextFieldWithoutM3(
                value = value,
                onValueChange = onValueChange,
                placeholder = defaultPlaceHolder(rowItem.placeHolder?.takeIf(String::isNotEmpty)),
                containerColor = FloconTheme.colorPalette.secondary,
                contentPadding = PaddingValues(horizontal = FloconTheme.spacing.extraSmall),
                modifier = Modifier
                    .weight(1f)
            )

            if (showSubmitButton) {
                FloconIconTonalButton(
                    onClick = onSubmit,
                    containerColor = FloconTheme.colorPalette.secondary,
                ) {
                    FloconIcon(
                        imageVector = Icons.AutoMirrored.Outlined.Send
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun DashboardTextFieldViewPreview_placeholder() {
    val rowItem = DashboardContainerViewState.RowItem.TextField(
        id = "1",
        label = "label",
        placeHolder = "placeholder",
        value = "",
    )
    FloconTheme {
        DashboardTextFieldView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.primary,
            ),
            rowItem = rowItem,
            value = "",
            onValueChange = {},
        )
    }
}

@Preview
@Composable
internal fun DashboardTextFieldViewPreview_withValue() {
    val rowItem = DashboardContainerViewState.RowItem.TextField(
        id = "1",
        label = "label",
        placeHolder = "placeholder",
        value = "value",
    )
    FloconTheme {
        DashboardTextFieldView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.primary,
            ),
            rowItem = rowItem,
            value = "value",
            onValueChange = {},
        )
    }
}
