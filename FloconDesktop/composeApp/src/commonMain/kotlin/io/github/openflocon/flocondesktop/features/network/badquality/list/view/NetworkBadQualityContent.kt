package io.github.openflocon.flocondesktop.features.network.badquality.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconActionButton
import io.github.openflocon.library.designsystem.components.floconMonoStyle
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.badquality_subtitle
import flocondesktop.composeapp.generated.resources.badquality_title
import flocondesktop.composeapp.generated.resources.general_create

@Composable
fun NetworkBadQualityContent(
    lines: ImmutableList<NetworkBadQualityLineUiModel>,
    onItemClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    setEnabled: (id: String?) -> Unit,
    onAddItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Column(
        modifier = modifier.background(colors.bg100),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
                ) {
                    Text(
                        text = stringResource(Res.string.badquality_title),
                        style = FloconTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.ink,
                    )
                    Text(
                        text = lines.size.toString(),
                        style = floconMonoStyle(small = true),
                        color = colors.inkMuted,
                        modifier = Modifier
                            .background(colors.bg300, CircleShape)
                            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
                    )
                }
                Text(
                    text = stringResource(Res.string.badquality_subtitle),
                    style = FloconTheme.typography.bodyMedium,
                    color = colors.inkMuted,
                )
            }
            FloconActionButton(
                onClick = onAddItemClicked,
                primary = true,
            ) {
                Text(stringResource(Res.string.general_create))
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.line)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.medium),
        ) {
            BadQualityHeaderView(Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            ) {
                items(lines) {
                    BadNetworkLineView(
                        item = it,
                        onClicked = onItemClicked,
                        onDeleteClicked = onDeleteClicked,
                        enableClicked = { id, enabled ->
                            if (enabled) {
                                setEnabled(id)
                            } else {
                                setEnabled(null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun BadQualityHeaderView(modifier: Modifier = Modifier) {
    val colors = FloconTheme.colors
    val scale = FloconTheme.scale
    // small capitals, like the titles of the groups of the design system
    val typo = FloconTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.06.em)

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp * scale)
                .padding(horizontal = FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        ) {
            HeaderText("Enabled", typo, Modifier.width(BadQualityColumns.enabled * scale))
            HeaderText("Name", typo, Modifier.weight(1f))
            HeaderText("Trigger", typo, Modifier.width(BadQualityColumns.trigger * scale))
            HeaderText("Latency", typo, Modifier.width(BadQualityColumns.latency * scale))
            HeaderText("Error rate", typo, Modifier.width(BadQualityColumns.errorRate * scale))
            HeaderText("Errors", typo, Modifier.width(BadQualityColumns.errors * scale))
            Box(Modifier.width(BadQualityColumns.actions * scale))
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
    modifier: Modifier,
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
