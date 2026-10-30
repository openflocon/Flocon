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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_column_not_null_short
import flocondesktop.composeapp.generated.resources.database_column_primary_key
import flocondesktop.composeapp.generated.resources.ic_db_key
import flocondesktop.composeapp.generated.resources.ic_db_number
import flocondesktop.composeapp.generated.resources.ic_db_text
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
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
        val icon = model.icon()
        Image(
            painter = painterResource(icon.drawable),
            modifier = Modifier.size(14.dp),
            colorFilter = ColorFilter.tint(icon.tint),
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
                    .background(FloconTheme.colors.bg300)
                    .padding(horizontal = FloconTheme.spacing.extraSmall),
            ) {
                Text(
                    stringResource(Res.string.database_column_not_null_short),
                    style = FloconTheme.typography.bodyMedium.copy(fontSize = 9.sp, lineHeight = 14.sp),
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

private class ColumnIcon(val drawable: DrawableResource, val tint: Color)

@Composable
private fun TableUiModel.ColumnUiModel.icon(): ColumnIcon {
    val colors = FloconTheme.colors
    if (isPrimaryKey) return ColumnIcon(Res.drawable.ic_db_key, colors.accent)
    val sqlType = type.uppercase()
    return when {
        NUMBER_TYPES.any { sqlType.contains(it) } -> ColumnIcon(Res.drawable.ic_db_number, colors.catNetwork)
        else -> ColumnIcon(Res.drawable.ic_db_text, colors.catDatabase)
    }
}

private val NUMBER_TYPES = listOf("INT", "REAL", "FLOA", "DOUB", "NUMERIC", "DECIMAL")
