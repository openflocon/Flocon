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

## Setup

Dashboards are part of the core module (`flocon` / `flocon-no-op`), install the plugin:

```kotlin
startFlocon(context) {
    install(FloconDashboard)
}
```

---

## Defining Dashboards

### Reactive Dashboard (Kotlin `Flow`)

A dashboard is attached to a `CoroutineScope`: it is updated while the scope is active, and every
section bound to a `Flow` is re-rendered on the desktop when the flow emits:

```kotlin
Flocon.dashboardPlugin.dashboard(id = "user_dashboard", scope = viewModelScope) {
    section(name = "Active User", flow = userFlow) { user ->
        text(label = "User ID", value = user.id)
        text(label = "Email", value = user.email)

        textField(
            id = "display_name",
            label = "Display Name",
            placeHolder = "John",
            value = user.displayName,
            onSubmitted = { newName ->
                userViewModel.updateName(newName)
            }
        )

        checkBox(
            id = "beta",
            label = "Beta Features Enabled",
            value = user.isBeta,
            onUpdated = { isEnabled ->
                userViewModel.setBeta(isEnabled)
            }
        )
    }

    section(name = "App Status") {
        text(label = "Environment", value = "Staging")
        label(label = "Build: #142")
        button(
            text = "Clear Image Cache",
            id = "clear_cache",
            onClick = { clearImageCache() }
        )
    }
}
```

`dashboard(...)` returns a `Job`: cancel it to stop updating the dashboard.

---

### Static Dashboard

```kotlin
Flocon.dashboardPlugin.registerDashboard(id = "main") {
    section(name = "App Status") {
        text(label = "Environment", value = "Staging")
        button(
            text = "Clear Image Cache",
            id = "clear_cache",
            onClick = { clearImageCache() }
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
Flocon.dashboardPlugin.dashboard(id = "settings", scope = scope) {
    form(
        name = "Server Configuration",
        submitText = "Apply Settings",
        onSubmitted = { values ->
            val host = values["host_input"]
            val mockMode = values["mock_mode"]
            applyNewConfig(host, mockMode)
        }
    ) {
        textField(id = "host_input", label = "API Host", placeHolder = null, value = "https://staging.api.com")
        checkBox(id = "mock_mode", label = "Enable Mock Mode", value = false)
    }
}
```
