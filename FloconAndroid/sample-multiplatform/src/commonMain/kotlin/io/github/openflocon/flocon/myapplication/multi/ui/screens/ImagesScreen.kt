package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen

private val images = List(100) { i -> "https://picsum.photos/id/$i/1000/1200" }

/** Every image is a network call, tagged as an image so the desktop previews it. */
@Composable
fun ImagesScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Images, onBack, scrollable = false) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(images) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
    }
}
