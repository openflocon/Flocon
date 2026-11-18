package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preferences_column_key
import flocondesktop.composeapp.generated.resources.preferences_column_type
import flocondesktop.composeapp.generated.resources.preferences_column_value
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import org.jetbrains.compose.resources.stringResource

internal const val KeyColumnWeight = 0.34f
internal const val ValueColumnWeight = 0.66f

/** The grid shared by the header and the rows: key | type | value | actions. */
@Composable
internal fun PreferenceGridRow(
    key: @Composable RowScope.() -> Unit,
    type: @Composable RowScope.() -> Unit,
    value: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FloconTheme.spacing.large),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        key()
        type()
        value()
        actions()
    }
}

@Composable
internal fun SharedPreferencesHeader(modifier: Modifier = Modifier) {
    PreferenceGridRow(
        modifier = modifier.floconTableHeader(),
        key = { FloconTableHeaderLabel(stringResource(Res.string.preferences_column_key), Modifier.weight(KeyColumnWeight)) },
        type = { FloconTableHeaderLabel(stringResource(Res.string.preferences_column_type), Modifier.width(FloconTheme.sizes.columnSmall)) },
        value = { FloconTableHeaderLabel(stringResource(Res.string.preferences_column_value), Modifier.weight(ValueColumnWeight)) },
        actions = { Row(Modifier.width(FloconTheme.sizes.columnSmall)) {} },
    )
}
