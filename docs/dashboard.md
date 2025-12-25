### 📈 Configurable Dashboards

<img width="1027" height="561" alt="Screenshot 2025-09-12 at 15 45 05" src="https://github.com/user-attachments/assets/056feafc-fda9-46ff-aaf0-4b8a0801b72e" />
<img width="373" height="312" alt="Screenshot 2025-09-12 at 15 44 57" src="https://github.com/user-attachments/assets/03b7ed4a-4de0-472d-87aa-850b33a3843f" />

Your Android application can define and expose **custom dashboards**, which Flocon renders dynamically in the desktop interface.

Use cases include:
- Displaying live business metrics
- Monitoring app state variables
- Debugging real-time values (e.g., geolocation, battery, app mode)
- Real-time in-app variables editing
- Performing mobile callbacks from the desktop app

#### Basic Usage

Dashboards are defined programmatically on the mobile side via the SDK. They can be static or update live as data changes.

```kotlin
floconDashboard(id = "main") {
    section(name = "App Info") {
        text(label = "Version", value = "1.0.0")
        label(label = "Status: Online")
        button(
            text = "Reset Cache",
            onClick = { /* Handle click */ }
        )
    }
}
```

#### Reactive Dashboards

You can bind a `section` to a Kotlin `Flow`. The section will automatically refresh in the Flocon Desktop app whenever the flow emits a new value.

```kotlin
floconDashboard(id = "user_dashboard") {
    section(name = "User Profile", userFlow) { user ->
        text(label = "Username", value = user.name)
        text(label = "Email", value = user.email)
        
        textField(
            label = "Update Display Name",
            value = user.displayName,
            onSubmitted = { newName ->
                userViewModel.updateName(newName)
            }
        )
        
        checkBox(
            label = "Beta Tester",
            value = user.isBeta,
            onUpdated = { enabled ->
                userViewModel.setBeta(enabled)
            }
        )
    }
}
```

#### Available Elements

Flocon provides several UI elements to build your dashboards:

| Element | Description |
| :--- | :--- |
| `text` | A labeled read-only text field. Supports custom colors. |
| `label` | A simple text label. |
| `button` | An actionable button that triggers a callback on the device. |
| `textField`| An input field that sends its content back to the device. |
| `checkBox` | A toggle switch for boolean values. |
| `plainText`| Optimized for displaying long strings or logs. |
| `json` | Renders a JSON string with syntax highlighting and tree view. |
| `markdown` | Renders rich text using Markdown syntax. |
| `html` | Renders basic HTML content. |

#### Forms

Use the `form` element to group multiple inputs with a single submit action.

```kotlin
floconDashboard(id = "settings") {
    form(
        name = "App Settings",
        submitText = "Save Changes",
        onSubmitted = { values ->
            val theme = values["theme_input"]
            val notifications = values["notif_check"]
            // Save settings...
        }
    ) {
        textField(id = "theme_input", label = "Theme Name", value = "Dark")
        checkBox(id = "notif_check", label = "Enable Notifications", value = true)
    }
}
```

#### Rich Content

You can also display rich content like Markdown or HTML.

```kotlin
floconDashboard(id = "docs") {
    section("Documentation") {
        markdown(
            label = "Release Notes",
            value = """
                # Version 2.0
                - Added **Reactive** support
                - New `form` element
            """.trimIndent()
        )
    }
}
```