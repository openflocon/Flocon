package io.github.openflocon.flocondesktop.features.network.badquality.edition.view


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun NetworkExceptionSelector(
    selected: String,
    onSelected: (errorClassPath: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        possibleExceptions.fastForEach { exception ->
            val (backgroundColor, textColor) = if (exception.classPath == selected) {
                FloconTheme.colorPalette.onSurface to FloconTheme.colorPalette.panel
            } else {
                FloconTheme.colorPalette.panel to FloconTheme.colorPalette.onSurface
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = backgroundColor,
                    )
                    .then(
                        Modifier.clickable(
                            onClick = {
                                onSelected(exception.classPath)
                            },
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = exception.description,
                    style = FloconTheme.typography.bodySmall,
                    color = textColor,
                )
                Text(
                    text = exception.classPath,
                    style = FloconTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = textColor,
                )
            }
        }
    }
}
