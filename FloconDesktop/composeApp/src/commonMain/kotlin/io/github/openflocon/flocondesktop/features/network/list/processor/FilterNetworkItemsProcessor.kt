package io.github.openflocon.flocondesktop.features.network.list.processor

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi

class FilterNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
        allowedMethods: List<NetworkMethodUi>,
    ): List<NetworkItemViewState> = items.asSequence()
        .filter { item ->
            item.second.method in allowedMethods
        }
        .map { it.second }
        .toList()
}
