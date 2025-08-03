package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.chevron_right
import flocondesktop.composeapp.generated.resources.network
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.view.components.FilterDropdown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.painterResource

class MethodFilter : Filters {

    private val selectedMethods = MutableStateFlow(emptyList<Method>())
    
    override val content: @Composable (() -> Unit) = {
        val methods by selectedMethods.collectAsStateWithLifecycle()

        FilterDropdown(
            text = "Method",
            icon = painterResource(Res.drawable.network) // TODO Change
        ) {
            Method.entries
                .forEach { method ->
                    val selected = methods.contains(method)

                    DropdownMenuItem(
                        text = { Text(text = method.text) },
                        trailingIcon = {
                            if (selected) {
                                Icon(
                                    painter = painterResource(Res.drawable.chevron_right), // TODO Change
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        onClick = {
                            if (selected)
                                remove(method)
                            else
                                add(method)
                        }
                    )
                }
        }
    }

    override fun filter(list: List<FloconHttpRequestDomainModel>): Flow<List<FloconHttpRequestDomainModel>> {
        return selectedMethods.map { methods ->
            if (methods.isEmpty())
                return@map list

            list.filter { methods.map(Method::text).contains(it.request.method) }
        }
    }

    private fun add(method: Method) {
        selectedMethods.update { it + method }
    }

    private fun remove(method: Method) {
        selectedMethods.update { it - method }
    }

    private enum class Method(val text: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        PATCH("PATCH"),
        DELETE("DELETE")
    }

}
