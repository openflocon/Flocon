package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel

@Immutable
data class FilterChipUiModel(
    val text: String,
    val type: FilterType
) {
    enum class FilterType {
        INCLUDE,
        EXCLUDE
    }
}

fun FilterChipUiModel.toDomain() = FilterQueryLogDomainModel(
    text = text,
    type = when (type) {
        FilterChipUiModel.FilterType.INCLUDE -> FilterQueryLogDomainModel.FilterType.INCLUDE
        FilterChipUiModel.FilterType.EXCLUDE -> FilterQueryLogDomainModel.FilterType.EXCLUDE
    }
)