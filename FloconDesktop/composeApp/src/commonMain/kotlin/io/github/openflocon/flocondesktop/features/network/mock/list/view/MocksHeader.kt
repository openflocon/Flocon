package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.openflocon.library.designsystem.FloconTheme

// the columns of the list of mocks, shared by its header and its lines
@Composable
fun MocksHeaderView(modifier: Modifier = Modifier) {
    val colors = FloconTheme.colors
    // small capitals, like the titles of the groups of the design system
    val typo = FloconTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.06.em)

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FloconTheme.sizes.headerHeight)
                .padding(horizontal = FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        ) {
            HeaderText("Enabled", typo, Modifier.width(FloconTheme.sizes.columnToggle))
            HeaderText("Method", typo, Modifier.width(FloconTheme.sizes.columnSmall))
            HeaderText("Name / Pattern", typo, Modifier.weight(1f))
            HeaderText("Shared", typo, Modifier.width(FloconTheme.sizes.columnSmall))
            Box(Modifier.width(FloconTheme.sizes.columnActions))
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.line)
        )
    }
}

@Composable
private fun HeaderText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        style = style,
        color = FloconTheme.colors.inkFaint,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}
