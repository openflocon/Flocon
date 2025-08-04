package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.openflocon.flocondesktop.features.network.ui.FilterUiState
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterDropdown

class MethodFilter : Filters {

    override fun filter(state: FilterUiState, list: List<NetworkItemViewState>): List<NetworkItemViewState> {
        if (state.methods.isEmpty())
            return list

        return list.filter { item ->
            when (item.method) {
                NetworkMethodUi.GraphQl.MUTATION -> state.methods.contains(NetworkMethodUi.GraphQl.MUTATION)
                NetworkMethodUi.GraphQl.QUERY -> state.methods.contains(NetworkMethodUi.GraphQl.QUERY)
                NetworkMethodUi.Grpc -> state.methods.contains(NetworkMethodUi.Grpc)
                NetworkMethodUi.Http.DELETE -> state.methods.contains(NetworkMethodUi.Http.DELETE)
                NetworkMethodUi.Http.GET -> state.methods.contains(NetworkMethodUi.Http.GET)
                NetworkMethodUi.Http.POST -> state.methods.contains(NetworkMethodUi.Http.POST)
                NetworkMethodUi.Http.PUT -> state.methods.contains(NetworkMethodUi.Http.PUT)
                is NetworkMethodUi.OTHER -> state.methods.contains(NetworkMethodUi.OTHER.EMPTY)
            }
        }
    }

}

@Composable
fun FilterMethods(
    filterState: FilterUiState,
    onAction: (NetworkAction) -> Unit
) {
    FilterDropdown(
        text = "Method",
        icon = null // TODO Find better icon
    ) {
        NetworkMethodUi.all()
            .forEach { method ->
                val selected = filterState.methods.contains(method)
                val onClick = { onAction(NetworkAction.FilterMethod(method, !selected)) }

                DropdownMenuItem(
                    text = { Text(text = method.label) },
                    leadingIcon = {},
                    trailingIcon = {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = { onClick() },
                            interactionSource = null
                        )
                    },
                    onClick = onClick
                )
            }
    }
}

private val NetworkMethodUi.label
    get() = when (this) {
        NetworkMethodUi.GraphQl.MUTATION -> "GraphQL - Mutation"
        NetworkMethodUi.GraphQl.QUERY -> "GraphQL - Query"
        NetworkMethodUi.Grpc -> "Grpc"
        NetworkMethodUi.Http.DELETE -> "Http - DELETE"
        NetworkMethodUi.Http.GET -> "Http - GET"
        NetworkMethodUi.Http.POST -> "Http - POST"
        NetworkMethodUi.Http.PUT -> "Http - PUT"
        is NetworkMethodUi.OTHER -> "Other"
    }
