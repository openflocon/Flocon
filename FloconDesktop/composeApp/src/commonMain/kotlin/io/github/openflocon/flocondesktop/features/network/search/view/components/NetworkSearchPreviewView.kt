package io.github.openflocon.flocondesktop.features.network.search.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextLayoutResult
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.flocondesktop.features.network.search.Match
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import kotlinx.coroutines.delay

@Composable
internal fun NetworkSearchPreviewView(
    request: FloconNetworkCallDomainModel,
    matches: List<Match>,
    currentMatchIndex: Int,
    onNextMatch: () -> Unit,
    onPrevMatch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.surface)
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${matches.size} matches",
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onSurface
            )

            Spacer(Modifier.width(16.dp))

            if (matches.isNotEmpty()) {
                Text(
                    text = "${currentMatchIndex + 1} / ${matches.size}",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onSurface
                )
                Spacer(Modifier.width(8.dp))
                FloconIcon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack, // Left Arrow
                    //contentDescription = "Previous Match",
                    modifier = Modifier.clickable { onPrevMatch() }
                )
                Spacer(Modifier.width(8.dp))
                FloconIcon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward, // Right Arrow
                    //contentDescription = "Next Match",
                    modifier = Modifier.clickable { onNextMatch() }
                )
            }

            Spacer(Modifier.weight(1f))

            FloconIcon(
                imageVector = Icons.Outlined.Close,
                //contentDescription = "Close Preview",
                modifier = Modifier.clickable { onClose() }
            )
        }

        // Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val body = request.responseBody() ?: "No response body"
            val scrollState = rememberScrollState()

            val annotatedString = remember(body, matches, currentMatchIndex) {
                 buildAnnotatedString {
                    append(body)
                    matches.forEachIndexed { index, match ->
                        addStyle(
                            style = SpanStyle(
                                background = if (index == currentMatchIndex) Color.Red.copy(alpha = 0.5f) else Color.Yellow.copy(alpha = 0.3f),
                                color = if (index == currentMatchIndex) Color.White else Color.Black
                            ),
                            start = match.start,
                            end = match.start + match.length
                        )
                    }
                }
            }

            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            LaunchedEffect(currentMatchIndex, matches, layoutResult) {
                if (matches.isNotEmpty()) {
                    val match = matches.getOrNull(currentMatchIndex)
                    if (match != null) {
                        layoutResult?.let { layout ->
                            val line = layout.getLineForOffset(match.start)
                            val y = layout.getLineTop(line)
                            scrollState.animateScrollTo(y.toInt())
                        }
                    }
                }
            }
            
            Text(
                text = annotatedString,
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = FloconTheme.colorPalette.onSurface,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                onTextLayout = {
                    layoutResult = it
                }
            )
        }
    }
}
