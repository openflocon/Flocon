package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkTag
import io.github.openflocon.flocondesktop.features.network.list.view.components.deleteMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.deleteMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.getMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.getMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.grpcMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.grpcMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.patchMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.patchMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.postMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.postMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.putMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.putMethodText
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun MockNetworkMethodView(
    method: MockNetworkMethodUi,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val (backgroundColor, textColor) =
        when (method) {
            MockNetworkMethodUi.DELETE -> deleteMethodBackground to deleteMethodText
            MockNetworkMethodUi.GET -> getMethodBackground to getMethodText
            MockNetworkMethodUi.PATCH -> patchMethodBackground to patchMethodText
            MockNetworkMethodUi.POST -> postMethodBackground to postMethodText
            MockNetworkMethodUi.PUT -> putMethodBackground to putMethodText
            MockNetworkMethodUi.ALL -> grpcMethodBackground to grpcMethodText
        }
    NetworkTag(
        text = method.text,
        backgroundColor = backgroundColor,
        textColor = textColor,
        textSize = FloconTheme.typography.labelLarge.fontSize,
        modifier = modifier,
        icon = null,
        contentPadding = PaddingValues(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        onClick = onClick,
    )
}
