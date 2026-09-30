package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sebastianneubauer.jsontree.diff.JsonTreeDiff
import com.sebastianneubauer.jsontree.diff.JsonTreeDiffColors
import com.sebastianneubauer.jsontree.diff.JsonTreeDiffError
import com.sebastianneubauer.jsontree.diff.JsonTreeDiffSuccess
import com.sebastianneubauer.jsontree.diff.defaultDarkDiffColors
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconJsonTreeDiff(
    originalJson: String,
    revisedJson: String,
    onSuccess: (JsonTreeDiffSuccess) -> Unit,
    onLoading: @Composable () -> Unit,
    onError: @Composable (JsonTreeDiffError) -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(8.dp),
    colors: JsonTreeDiffColors = defaultDarkDiffColors,
    showInlineDiffs: Boolean = false,
    textStyle: TextStyle = FloconTheme.typography.bodyMedium,
) {
    JsonTreeDiff(
        modifier = modifier,
        originalJson = originalJson,
        revisedJson = revisedJson,
        contentPadding = paddingValues,
        showInlineDiffs = showInlineDiffs,
        colors = colors,
        textStyle = textStyle,
        onLoading = onLoading,
        onError = onError,
        onSuccess = onSuccess
    )
}
