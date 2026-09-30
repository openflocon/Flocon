package io.github.openflocon.flocondesktop.features.network.body

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianneubauer.jsontree.diff.JsonTreeDiffInfo
import com.sebastianneubauer.jsontree.diff.defaultDarkDiffColors
import io.github.openflocon.flocondesktop.features.network.body.model.NetworkDiffUi
import io.github.openflocon.flocondesktop.features.network.body.model.previewNetworkDiffUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconJsonTreeDiff
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkDiffWindow(
    diff: NetworkDiffUi
) {
    NetworkDiffContent(
        diff = diff,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun NetworkDiffContent(
    diff: NetworkDiffUi,
    modifier: Modifier = Modifier,
) {
    var jsonTreeDiffInfo by remember(diff) { mutableStateOf<JsonTreeDiffInfo?>(null) }

    FloconSurface(
        modifier = modifier,
    ) {
        Column {
            val diffInfo = jsonTreeDiffInfo

            if(diffInfo != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .padding(start = 8.dp),
                        text = "Original:",
                        style = FloconTheme.typography.titleMedium
                    )

                    Row(modifier = Modifier.weight(1F)) {
                        Text(
                            modifier = Modifier.weight(1F),
                            text = "Clipboard:",
                            style = FloconTheme.typography.titleMedium
                        )

                        Text(
                            text = "+${diffInfo.changeInfo.insertions} -${diffInfo.changeInfo.deletions}",
                            style = FloconTheme.typography.titleMedium
                        )
                    }
                }
            }

            FloconJsonTreeDiff(
                modifier = Modifier.fillMaxSize(),
                originalJson = diff.json,
                revisedJson = diff.clipboardJson,
                paddingValues = PaddingValues(8.dp),
                colors = defaultDarkDiffColors.copy(
                    regularBackgroundColor = FloconTheme.colorPalette.surface
                ),
                onSuccess = { jsonTreeDiffInfo = it.info },
                onLoading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FloconCircularProgressIndicator()
                    }
                },
                onError = {
                    when(it.error) {
                        is OriginalJsonError -> {
                            Text(
                                text = "Error in original Json:\n${it.error.throwable.localizedMessage}",
                                style = FloconTheme.typography.bodyMedium
                            )
                        }
                        is RevisedJsonError -> {
                            Text(
                                text = "Error in clipboard Json:\n${it.error.throwable.localizedMessage}",
                                style = FloconTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun NetworkDiffContentPreview() {
    FloconTheme {
        NetworkDiffContent(
            diff = previewNetworkDiffUi()
        )
    }
}
