---
title: Reactive Custom Dashboards
description: Build interactive mobile debug dashboards rendered on your desktop in real time.
---

# 📈 Reactive Custom Dashboards

Your application can expose **custom dashboards** defined in Kotlin, which Flocon renders dynamically into interactive desktop controls.

---

## Overview

<img width="1027" height="561" alt="Desktop Dashboard View" src="https://github.com/user-attachments/assets/056feafc-fda9-46ff-aaf0-4b8a0801b72e" style="border-radius: 8px;" />

<img width="373" height="312" alt="Mobile Dashboard View" src="https://github.com/user-attachments/assets/03b7ed4a-4de0-472d-87aa-850b33a3843f" style="border-radius: 8px; margin-top: 1rem;" />

Use cases:
- Toggle feature flags or mock environments.
- Display live business metrics and battery/network health.
- Mutate state variables on-the-fly without rebuilding.
- Trigger in-app test actions (reset caches, seed test data, trigger notifications).

---

## Defining Dashboards

### Static Dashboard

```kotlin
floconDashboard(id = "main") {
    section(name = "App Status") {
        text(label = "Environment", value = "Staging")
        label(label = "Build: #142")
        button(
            text = "Clear Image Cache",
            onClick = { clearImageCache() }
        )
    }
}
```

---

### Reactive Dashboard (Kotlin `Flow`)

You can bind a dashboard section directly to a Kotlin `StateFlow` or `Flow`. The desktop UI updates automatically whenever new data is emitted:

```kotlin
floconDashboard(id = "user_dashboard") {
    section(name = "Active User", userFlow) { user ->
        text(label = "User ID", value = user.id)
        text(label = "Email", value = user.email)
        
        textField(
            label = "Display Name",
            value = user.displayName,
            onSubmitted = { newName ->
                userViewModel.updateName(newName)
            }
        )
        
        checkBox(
            label = "Beta Features Enabled",
            value = user.isBeta,
            onUpdated = { isEnabled ->
                userViewModel.setBeta(isEnabled)
            }
        )
    }
}
```

---

## Available UI Components

| Element | Description |
| :--- | :--- |
| `text` | Read-only labeled text (supports custom hex/RGB colors). |
| `label` | Simple heading or status text. |
| `button` | Actionable button that executes a callback inside your running app. |
| `textField` | Text input that sends entered values back to the app on submit. |
| `checkBox` | Toggle switch for boolean properties. |
| `plainText` | Optimized scrolling container for multi-line logs and text dumps. |
| `json` | Formatted JSON tree visualizer with expand/collapse. |
| `markdown` | Renders rich text with Markdown formatting. |
| `html` | Renders styled HTML snippets. |

---

## Multi-input Forms

Group inputs together with a single submission action:

```kotlin
floconDashboard(id = "settings") {
    form(
        name = "Server Configuration",
        submitText = "Apply Settings",
        onSubmitted = { values ->
            val host = values["host_input"]
            val mockMode = values["mock_mode"]
            applyNewConfig(host, mockMode)
        }
    ) {
        textField(id = "host_input", label = "API Host", value = "https://staging.api.com")
        checkBox(id = "mock_mode", label = "Enable Mock Mode", value = false)
    }
}
```