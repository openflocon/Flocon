package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import kotlinx.coroutines.flow.Flow

@Stable
interface Filters {
    val sort: Int
    val content: @Composable () -> Unit

    fun filter(list: List<FloconHttpRequestDomainModel>): Flow<List<FloconHttpRequestDomainModel>>

}
