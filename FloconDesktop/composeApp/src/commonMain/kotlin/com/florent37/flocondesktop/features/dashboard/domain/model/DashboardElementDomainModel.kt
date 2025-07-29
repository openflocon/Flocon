package com.florent37.flocondesktop.features.dashboard.domain.model

sealed interface DashboardElementDomainModel {
    data class Button(
        val text: String,
        val id: String,
    ) : DashboardElementDomainModel

    data class Text(
        val label: String,
        val value: String,
        val color: Int?,
    ) : DashboardElementDomainModel

    data class PlainText(
        val label: String,
        val value: String,
        val type: Type,
    ) : DashboardElementDomainModel {
        enum class Type {
            Text,
            Json,
        }
    }

    data class TextField(
        val label: String,
        val placeHolder: String?,
        val value: String,
        val id: String,
    ) : DashboardElementDomainModel

    data class CheckBox(
        val label: String,
        val value: Boolean,
        val id: String,
    ) : DashboardElementDomainModel
}
