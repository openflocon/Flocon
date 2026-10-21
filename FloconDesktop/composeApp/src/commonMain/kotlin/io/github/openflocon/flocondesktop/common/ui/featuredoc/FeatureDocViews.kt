package io.github.openflocon.flocondesktop.common.ui.featuredoc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_doc_how_to_setup
import flocondesktop.composeapp.generated.resources.feature_doc_open_documentation
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconEmptyState
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * Small (i) button, always visible on a page, opening its documentation.
 */
@Composable
fun FeatureDocButton(
    doc: FeatureDoc,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    FloconIconButton(
        imageVector = Icons.Outlined.Info,
        tooltip = stringResource(Res.string.feature_doc_open_documentation),
        onClick = { runCatching { uriHandler.openUri(doc.url) } },
        modifier = modifier.pointerHoverIcon(PointerIcon.Hand),
    )
}

/**
 * Placeholder displayed when a page has no content: illustration of the feature,
 * a short explanation and a "How to set up" button opening the setup section of its documentation.
 */
@Composable
fun FeatureEmptyState(
    doc: FeatureDoc,
    modifier: Modifier = Modifier,
    illustrationSize: Int = 160,
) {
    val uriHandler = LocalUriHandler.current
    FloconEmptyState(
        title = doc.emptyTitle?.let { stringResource(it) }.orEmpty(),
        description = doc.emptyDescription?.let { stringResource(it) }.orEmpty(),
        modifier = modifier,
        illustration = {
            FeatureIllustration(
                doc = doc,
                modifier = Modifier.size(width = illustrationSize.dp, height = (illustrationSize * 0.75f).dp),
            )
        },
        action = {
            FloconButton(
                onClick = { runCatching { uriHandler.openUri(doc.setupUrl) } },
                containerColor = FloconTheme.colors.accent,
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Description,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(text = stringResource(Res.string.feature_doc_how_to_setup))
                }
            }
        },
    )
}

@Preview
@Composable
private fun FeatureEmptyStatePreview() {
    FloconTheme {
        FeatureEmptyState(doc = FeatureDoc.Network)
    }
}
