package io.github.openflocon.flocondesktop.features.grpc.ui.view.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.bin
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterBar
import org.jetbrains.compose.resources.painterResource

@Composable
fun GrpcFilterBar(
    grpcItems: List<GrpcItemViewState>,
    onItemsChange: (List<GrpcItemViewState>) -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterText by remember {
        mutableStateOf("")
    }
    val onItemsChangeCallback by rememberUpdatedState(onItemsChange)
    val filteredGrpcItems: List<GrpcItemViewState> =
        remember(grpcItems, filterText) {
            if (filterText.isBlank()) {
                grpcItems
            } else {
                grpcItems.filter {
                    it.contains(filterText)
                }
            }
        }

    LaunchedEffect(filteredGrpcItems) {
        onItemsChangeCallback(filteredGrpcItems)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterBar(
            placeholderText = "Filter",
            modifier = Modifier.weight(1f),
            onTextChange = {
                filterText = it
            },
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onResetClicked)
                .padding(all = 8.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.bin),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
