### 📋 Structured Data Tables

<img width="1196" height="768" alt="tables" src="https://github.com/user-attachments/assets/ff3090fa-8f37-4138-a492-20b9159314af" />

In addition to dashboards, Flocon supports structured **data tables** (compatible with Kotlin Multiplatform). These allow you to visualize lists of items with multiple columns directly in the desktop interface.

Use cases include:
- Displaying active user sessions.
- Inspecting items in local memory or cache.
- Monitoring custom business events.
- Comparing simulated backend responses.

Tables are interactive, scrollable, and provide a clear way to inspect real-time collections.

#### Usage

To log a new row in a table, identify the table by name and use the `log` method with the `toParam` DSL to define column values.

```kotlin
floconTable("active_sessions").log(
    "user_id" toParam "1024",
    "name" toParam "Florent",
    "status" toParam "Active",
    "last_seen" toParam "2023-10-27 10:00"
)
```

#### Real-time Updates

Whenever you call `.log()`, a new row is appended to the corresponding table in Flocon Desktop. This makes it ideal for tracking events or state changes over time.

```kotlin
fun onUserAction(user: User, action: String) {
    floconTable("user_actions").log(
        "timestamp" toParam currentTimeMillis().toString(),
        "user" toParam user.name,
        "action" toParam action
    )
}
```