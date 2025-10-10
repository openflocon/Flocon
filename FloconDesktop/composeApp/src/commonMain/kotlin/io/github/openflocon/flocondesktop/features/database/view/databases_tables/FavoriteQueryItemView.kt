package io.github.openflocon.flocondesktop.features.database.view.databases_tables


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import io.github.openflocon.flocondesktop.common.ui.interactions.hover
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun FavoriteQueryItemView(
    state: DatabaseFavoriteQueryUiModel,
    onClick: (DatabaseFavoriteQueryUiModel) -> Unit,
    onDelete: (DatabaseFavoriteQueryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (background, textColor) = Color.Transparent to FloconTheme.colorPalette.onSurface

    var isHover by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .hover(
                isHover = {
                    isHover = it
                }
            )
            .clickable(onClick = {
                onClick(state)
            }
            ).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            modifier = Modifier.width(14.dp),
            imageVector = Icons.Outlined.Star,
            contentDescription = null,
            colorFilter = ColorFilter.tint(textColor),
        )
        Text(
            state.title,
            color = textColor,
            style = FloconTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(4.dp))

        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = isHover,
        ) {
            Image(
                modifier = Modifier.width(14.dp)
                    .clickable {
                        onDelete(state)
                    },
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                colorFilter = ColorFilter.tint(textColor),
            )
        }
    }
}