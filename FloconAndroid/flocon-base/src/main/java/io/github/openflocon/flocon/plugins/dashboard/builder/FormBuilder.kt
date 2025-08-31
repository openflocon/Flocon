package io.github.openflocon.flocon.plugins.dashboard.builder

import io.github.openflocon.flocon.plugins.dashboard.model.config.FormConfig

class FormBuilder(
    val name: String,
    val submitText: String,
    val onSubmitted: (Map<String, String>) -> Unit,
) : ContainerBuilder() {

    override fun build(): FormConfig {
        return FormConfig(
            id = "form_$name",
            name = name,
            submitText = submitText,
            elements = elements,
            onSubmitted = onSubmitted
        )
    }
}