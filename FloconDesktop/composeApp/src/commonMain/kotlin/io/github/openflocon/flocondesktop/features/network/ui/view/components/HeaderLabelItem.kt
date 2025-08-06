package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.arrow_up_cropped
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HeaderLabelItem(
    text: String,
    modifier: Modifier = Modifier,
    labelAlignment: Alignment = Alignment.Center,
) {
    val typo = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val textColor = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Image(
            imageVector = Icons.Default.FilterAlt,
            contentDescription = "Filter",
            colorFilter = ColorFilter.tint(textColor),
            modifier = Modifier.size(14.dp),
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = labelAlignment,
        ) {
            Text(
                text = text,
                style = typo,
                textAlign = TextAlign.Center,
                color = textColor,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            val iconsSize = 8.dp

            // image to not have paddings
            Box(modifier = Modifier.size(14.dp), contentAlignment = Alignment.BottomCenter) {
                Image(
                    painter = painterResource(Res.drawable.arrow_up_cropped),
                    contentDescription = "Sort ascending",
                    colorFilter = ColorFilter.tint(textColor),
                    modifier = Modifier.size(iconsSize)
                )
            }
            Box(modifier = Modifier.size(14.dp), contentAlignment = Alignment.TopCenter) {
                Image(
                    painter = painterResource(Res.drawable.arrow_up_cropped),
                    contentDescription = "Sort descending",
                    colorFilter = ColorFilter.tint(textColor),
                    modifier = Modifier.size(iconsSize)
                        .rotate(180f)
                )
            }
        }
    }
}

@Composable
@Preview
private fun HeaderLabelItemPreview() {
    FloconTheme {
        HeaderLabelItem(
            text = "test",
        )
    }
}
