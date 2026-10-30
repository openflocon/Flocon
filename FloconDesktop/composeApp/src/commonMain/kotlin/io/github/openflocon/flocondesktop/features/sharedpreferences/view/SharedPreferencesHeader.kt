package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preferences_column_key
import flocondesktop.composeapp.generated.resources.preferences_column_type
import flocondesktop.composeapp.generated.resources.preferences_column_value
import io.github.openflocon.library.designsystem.FloconTheme
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
    val style = FloconTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em)

    PreferenceGridRow(
        modifier = modifier
            .height(FloconTheme.sizes.headerHeight)
            .background(FloconTheme.colors.bg000.copy(alpha = 0.5f)),
        key = { HeaderLabel(stringResource(Res.string.preferences_column_key), style, Modifier.weight(KeyColumnWeight)) },
        type = { HeaderLabel(stringResource(Res.string.preferences_column_type), style, Modifier.width(FloconTheme.sizes.columnSmall)) },
        value = { HeaderLabel(stringResource(Res.string.preferences_column_value), style, Modifier.weight(ValueColumnWeight)) },
        actions = { Row(Modifier.width(FloconTheme.sizes.columnSmall)) {} },
    )
}

@Composable
private fun HeaderLabel(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        style = style,
        color = FloconTheme.colors.inkMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}
