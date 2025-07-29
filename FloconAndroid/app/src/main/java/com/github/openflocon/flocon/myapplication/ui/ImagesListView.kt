package com.github.openflocon.flocon.myapplication.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ImagesListView(modifier: Modifier = Modifier) {
    val images by remember {
        mutableStateOf(
            List(100) { i ->
                "https://picsum.photos/id/$i/1000/1200"
            }
        )
    }
    LazyColumn(modifier = modifier) {
        items(images) {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}