### 📈 Configurable Dashboards (from the mobile app)

<img width="1027" height="561" alt="Screenshot 2025-09-12 at 15 45 05" src="https://github.com/user-attachments/assets/056feafc-fda9-46ff-aaf0-4b8a0801b72e" />
<img width="373" height="312" alt="Screenshot 2025-09-12 at 15 44 57" src="https://github.com/user-attachments/assets/03b7ed4a-4de0-472d-87aa-850b33a3843f" />

Your Android application can define and expose **custom dashboards**, which Flocon renders dynamically in the desktop interface.

Use cases include:

- Displaying live business metrics
- Monitoring app state variables
- Debugging real-time values (e.g., geolocation, battery, app mode)
- Real time in-app variables editions
- Perform from the desktop app mobile callbacks

Dashboards are defined programmatically on the mobile side via the SDK, and they update live as data changes — making them ideal for live demos, QA testing, or in-field diagnostics.

```kotlin
userFlow.collect { user ->
     Flocon.dashboard(id = "main") {
        user?.let {
            section(name = "User") {
                text(label = "username", value = user.userName)
                text(label = "fullName", value = user.fullName, color = Color.Red.toArgb())
                text(label = "user id", value = user.id)
                button(
                    text = "Change User Id",
                    id = "changeUserId",
                    onClick = {
                        userFlow.update { it.copy(userName = "__flo__") }
                    }
                )
                textField(
                    label = "Update Name",
                    placeHolder = "name",
                    id = "changeUserName",
                    value = user.fullName,
                    onSubmitted = { value ->
                        userFlow.update { it.copy(fullName = value) }
                    })
            }
        }
    }
}
```