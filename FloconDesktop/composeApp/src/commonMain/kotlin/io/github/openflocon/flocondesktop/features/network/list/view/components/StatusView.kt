package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconStatusPill
import io.github.openflocon.library.designsystem.components.floconMonoStyle
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * The color of a status: an http code is colored by family (1xx info, 2xx success, 4xx warning, 5xx error),
 * the other statuses (exception, graphQl, gRPC…) by what they say.
 */
@Composable
internal fun NetworkStatusUi.color(): Color {
    val colors = FloconTheme.colors
    return text.toIntOrNull()?.let { colors.httpStatus(it) } ?: when (status) {
        NetworkStatusUi.Status.SUCCESS -> colors.statusSuccess
        NetworkStatusUi.Status.ERROR,
        NetworkStatusUi.Status.EXCEPTION -> colors.statusError

        NetworkStatusUi.Status.LOADING -> colors.inkMuted
    }
}

@Composable
fun StatusView(
    status: NetworkStatusUi,
    textSize: TextUnit = FloconTheme.typography.labelMedium.fontSize,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (status.status) {
            NetworkStatusUi.Status.SUCCESS,
            NetworkStatusUi.Status.ERROR,
            NetworkStatusUi.Status.EXCEPTION -> {
                val color = status.color()
                FloconStatusPill(color = color) {
                    SelectionContainer {
                        BasicText(
                            text = status.text,
                            autoSize = TextAutoSize.StepBased(
                                maxFontSize = textSize,
                                minFontSize = FloconTheme.typography.labelSmall.fontSize * 0.8f,
                            ),
                            maxLines = 1,
                            style = floconMonoStyle(small = true).copy(
                                color = color,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            }

            NetworkStatusUi.Status.LOADING -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                FloconLinearProgressIndicator(
                    modifier = Modifier
                        .width(50.dp),
                )
            }
        }
    }
}

@Composable
@Preview
private fun StatusView_Preview() {
    FloconTheme {
        StatusView(
            status =
            NetworkStatusUi(
                text = "200",
                status = NetworkStatusUi.Status.SUCCESS,
            ),
        )
    }
}
