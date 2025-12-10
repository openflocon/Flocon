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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.flocondesktop.features.network.search.Match
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon

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
    val locationLabel = remember(matches, currentMatchIndex) {
        if (matches.isNotEmpty() && matches.indices.contains(currentMatchIndex)) {
            matches[currentMatchIndex].location.label
        } else {
            "Preview"
        }
    }
    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.primary)
    ) {
        // Divider
        FloconHorizontalDivider(Modifier.fillMaxWidth())

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
                color = FloconTheme.colorPalette.onPrimary
            )

            Spacer(Modifier.width(16.dp))

            if (matches.isNotEmpty()) {
                Text(
                    text = "${currentMatchIndex + 1} / ${matches.size}",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPrimary
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

            Text(
                text = locationLabel,
                style = FloconTheme.typography.titleSmall,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.weight(1f))

            FloconIcon(
                imageVector = Icons.Outlined.Close,
                //contentDescription = "Close Preview",
                modifier = Modifier.clickable { onClose() }
            )
        }

        // Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val content = remember(matches, currentMatchIndex, request) {
                if (matches.isNotEmpty() && matches.indices.contains(currentMatchIndex)) {
                    matches[currentMatchIndex].content
                } else {
                    request.responseBody() ?: "No content"
                }
            }

            val scrollState = rememberScrollState()

            val annotatedString = remember(content, matches, currentMatchIndex) {
                buildAnnotatedString {
                    append(content)
                    // Highlighting
                    if (matches.isNotEmpty()) {
                        val currentMatch = matches.getOrNull(currentMatchIndex)
                        // Only highlight matches that belong to the SAME location/content
                        matches.filter { it.location == currentMatch?.location }.forEach { match ->
                            addStyle(
                                style = SpanStyle(
                                    background = if (match == currentMatch) Color.Red.copy(alpha = 0.5f) else Color.Yellow.copy(
                                        alpha = 0.3f
                                    ),
                                    color = if (match == currentMatch) Color.White else Color.Black
                                ),
                                start = match.start,
                                end = match.start + match.length
                            )
                        }
                    }
                }
            }

            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            // Scroll to top when switching content or match
            LaunchedEffect(currentMatchIndex) {
                if (matches.isNotEmpty()) {
                    val match = matches.getOrNull(currentMatchIndex)
                    if (match != null) {
                        layoutResult?.let { layout ->
                            // Ensure the offset is valid for the current content
                            if (match.start < layout.layoutInput.text.length) {
                                val line = layout.getLineForOffset(match.start)
                                val y = layout.getLineTop(line)
                                scrollState.animateScrollTo(y.toInt())
                            }
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
