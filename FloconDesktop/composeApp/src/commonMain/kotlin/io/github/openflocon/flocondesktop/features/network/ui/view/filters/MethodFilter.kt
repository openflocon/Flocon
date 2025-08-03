package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.FilterUiState
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterDropdown

class MethodFilter : Filters {

    override fun filter(state: FilterUiState, list: List<FloconHttpRequestDomainModel>): List<FloconHttpRequestDomainModel> {
        if (state.methods.isEmpty())
            return list

        return list.filter { item ->
            when (item.type) {
                is FloconHttpRequestDomainModel.Type.GraphQl -> state.methods.contains(
                    Methods.GraphQL
                )

                is FloconHttpRequestDomainModel.Type.Grpc -> state.methods.contains(Methods.Grpc)
                is FloconHttpRequestDomainModel.Type.Http -> state.methods.filterIsInstance<Methods.Http>()
                    .map(Methods.Http::methodName)
                    .contains(item.request.method)
            }
        }
    }

    sealed interface Methods {

        enum class Http(
            val methodName: String
        ) : Methods {
            GET("GET"),
            POST("POST"),
            PUT("PUT"),
            PATCH("PATCH"),
            DELETE("DELETE")
        }

        data object GraphQL : Methods

        data object Grpc : Methods

        companion object {
            fun all(): List<Methods> = Http.entries + GraphQL + Grpc
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
        MethodFilter.Methods.all()
            .forEach { method ->
                val selected = filterState.methods.contains(method)
                val onClick = { onAction(NetworkAction.FilterMethod(method, !selected)) }

                DropdownMenuItem(
                    text = { Text(text = method.label) },
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

private val MethodFilter.Methods.label
    get() = when (this) {
        MethodFilter.Methods.GraphQL -> "GraphQL"
        MethodFilter.Methods.Grpc -> "GRPC"
        MethodFilter.Methods.Http.GET -> "Http - GET"
        MethodFilter.Methods.Http.POST -> "Http - POST"
        MethodFilter.Methods.Http.PUT -> "Http - PUT"
        MethodFilter.Methods.Http.PATCH -> "Http - PATCH"
        MethodFilter.Methods.Http.DELETE -> "Http - DELETE"
    }
