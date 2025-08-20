package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkTag
import io.github.openflocon.flocondesktop.features.network.list.view.components.deleteMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.deleteMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.getMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.getMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.grpcMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.grpcMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.otherMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.otherMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.postMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.postMethodText
import io.github.openflocon.flocondesktop.features.network.list.view.components.putMethodBackground
import io.github.openflocon.flocondesktop.features.network.list.view.components.putMethodText
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi

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
            MockNetworkMethodUi.PATCH -> otherMethodBackground to otherMethodText
            MockNetworkMethodUi.POST -> postMethodBackground to postMethodText
            MockNetworkMethodUi.PUT -> putMethodBackground to putMethodText
            MockNetworkMethodUi.ALL -> grpcMethodBackground to grpcMethodText
        }
    NetworkTag(
        text = method.text,
        backgroundColor = backgroundColor,
        textColor = textColor,
        textSize = 14.sp,
        modifier = modifier,
        icon = null,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        onClick = onClick,
    )
}
