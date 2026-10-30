package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.openflocon.flocondesktop.features.images.model.ImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel

@Composable
fun ImageViewer(
    model: ImageViewerUiModel,
    onImageSelected: (id: String) -> Unit,
    onCopyUrl: (url: String) -> Unit,
    onDownload: (model: ImagesUiModel) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        ImageViewerContent(
            model = model,
            onImageSelected = onImageSelected,
            onCopyUrl = { onCopyUrl(model.current.url) },
            onOpenInBrowser = { uriHandler.openUri(model.current.url) },
            onDownload = { onDownload(model.current) },
            onClose = onClose,
            modifier = modifier.fillMaxSize(),
        )
    }
}
