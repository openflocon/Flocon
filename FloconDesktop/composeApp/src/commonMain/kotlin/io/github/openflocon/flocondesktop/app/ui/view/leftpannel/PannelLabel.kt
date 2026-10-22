package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun PannelLabel(
    text: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    isCollapsed: Boolean? = null, // null: the section can not be collapsed
    onClick: () -> Unit = {},
) {
    Crossfade(
        targetState = expanded,
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .then(if (isCollapsed != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (it) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // overline style
                    Text(
                        modifier = Modifier.weight(1f),
                        text = text.uppercase(),
                        style = FloconTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.06.em,
                            color = FloconTheme.colors.inkFaint,
                        ),
                    )
                    if (isCollapsed != null) {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            imageVector = if (isCollapsed) Icons.Outlined.ChevronRight else Icons.Outlined.ExpandMore,
                            contentDescription = if (isCollapsed) "Expand" else "Collapse",
                            tint = FloconTheme.colors.inkFaint,
                        )
                    }
                }
            } else {
                HorizontalDivider()
            }
        }
    }
}
