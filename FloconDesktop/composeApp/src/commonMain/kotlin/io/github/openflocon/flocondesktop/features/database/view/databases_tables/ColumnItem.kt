package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_column_not_null_short
import flocondesktop.composeapp.generated.resources.database_column_primary_key
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnItem(
    model: TableUiModel.ColumnUiModel,
    onClicked: (TableUiModel.ColumnUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClicked(model) }
            .padding(horizontal = FloconTheme.spacing.small)
            .padding(vertical = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Image(
            imageVector = model.icon(),
            modifier = Modifier.size(14.dp),
            colorFilter = ColorFilter.tint(
                if (model.isPrimaryKey) FloconTheme.colors.accent else FloconTheme.colors.inkMuted,
            ),
            contentDescription = if (model.isPrimaryKey) stringResource(Res.string.database_column_primary_key) else null,
        )
        Text(
            model.name,
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (model.isNotNull) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(FloconTheme.colors.line)
                    .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
            ) {
                Text(
                    stringResource(Res.string.database_column_not_null_short),
                    style = FloconTheme.typography.bodyMedium.copy(fontSize = 9.sp),
                    color = FloconTheme.colors.inkMuted,
                )
            }
        }
        Text(
            model.type,
            style = FloconTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
            ),
            color = FloconTheme.colors.inkMuted,
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}

private fun TableUiModel.ColumnUiModel.icon(): ImageVector {
    if (isPrimaryKey) return Icons.Filled.VpnKey
    val sqlType = type.uppercase()
    return when {
        NUMBER_TYPES.any { sqlType.contains(it) } -> Icons.Outlined.Tag
        TEXT_TYPES.any { sqlType.contains(it) } -> Icons.Outlined.Notes
        else -> Icons.Outlined.ViewColumn
    }
}

private val NUMBER_TYPES = listOf("INT", "REAL", "FLOA", "DOUB", "NUMERIC", "DECIMAL")
private val TEXT_TYPES = listOf("CHAR", "TEXT", "CLOB")
