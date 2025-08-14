package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.view.components.NetworkTag
import io.github.openflocon.flocondesktop.features.network.ui.view.components.deleteMethodBackground
import io.github.openflocon.flocondesktop.features.network.ui.view.components.deleteMethodText
import io.github.openflocon.flocondesktop.features.network.ui.view.components.getMethodBackground
import io.github.openflocon.flocondesktop.features.network.ui.view.components.getMethodText
import io.github.openflocon.flocondesktop.features.network.ui.view.components.grpcMethodBackground
import io.github.openflocon.flocondesktop.features.network.ui.view.components.grpcMethodText
import io.github.openflocon.flocondesktop.features.network.ui.view.components.otherMethodBackground
import io.github.openflocon.flocondesktop.features.network.ui.view.components.otherMethodText
import io.github.openflocon.flocondesktop.features.network.ui.view.components.postMethodBackground
import io.github.openflocon.flocondesktop.features.network.ui.view.components.postMethodText
import io.github.openflocon.flocondesktop.features.network.ui.view.components.putMethodBackground
import io.github.openflocon.flocondesktop.features.network.ui.view.components.putMethodText

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
        modifier = modifier.widthIn(min = 50.dp),
        icon = null,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        onClick = onClick,
    )
}
