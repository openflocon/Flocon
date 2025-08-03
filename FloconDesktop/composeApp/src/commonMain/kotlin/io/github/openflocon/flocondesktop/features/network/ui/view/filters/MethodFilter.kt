package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.network
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterDropdown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.painterResource

class MethodFilter : Filters {

    private val selectedMethods = MutableStateFlow(Methods.all())

    override val content: @Composable (() -> Unit) = {
        val methods by selectedMethods.collectAsStateWithLifecycle()

        FilterDropdown(
            text = "Method",
            icon = painterResource(Res.drawable.network) // TODO Change
        ) {
            Methods.all()
                .forEach { method ->
                    val selected = methods.contains(method)
                    val interactionSource = remember { MutableInteractionSource() }
                    val onClick = {
                        if (selected)
                            remove(method)
                        else
                            add(method)
                    }

                    DropdownMenuItem(
                        text = { Text(text = method.label) },
                        interactionSource = interactionSource,
                        trailingIcon = {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = { onClick() },
                                interactionSource = interactionSource
                            )
                        },
                        onClick = onClick
                    )
                }
        }
    }

    override fun filter(list: List<FloconHttpRequestDomainModel>): Flow<List<FloconHttpRequestDomainModel>> {
        return selectedMethods.map { methods ->
            if (methods.isEmpty())
                return@map list

            list.filter { item ->
                when (item.type) {
                    is FloconHttpRequestDomainModel.Type.GraphQl -> methods.contains(Methods.GraphQL)
                    is FloconHttpRequestDomainModel.Type.Grpc -> methods.contains(Methods.Grpc)
                    is FloconHttpRequestDomainModel.Type.Http -> methods.filterIsInstance<Methods.Http>()
                        .map(Methods.Http::query)
                        .contains(item.request.method)
                }
            }
        }
    }

    private fun add(method: Methods) {
        selectedMethods.update { it + method }
    }

    private fun remove(method: Methods) {
        selectedMethods.update { it - method }
    }

    sealed interface Methods {

        enum class Http(
            val query: String
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
