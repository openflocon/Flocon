### 🗝 Preferences Explorer & Editor

<img width="1295" height="836" alt="Screenshot 2025-09-12 at 15 41 04" src="https://github.com/user-attachments/assets/03c3278b-dc2f-4943-ba17-b18030e204ea" />

Flocon provides complete access to your app’s preferences (SharedPreferences and DataStore), which often store user tokens, feature flags, configuration options, and more.

Key capabilities include:
- **Automatic detection** of all application SharedPreferences.
- Viewing and filtering key-value pairs.
- Inspecting primitive values and JSON structures.
- **Editing values on the fly** from the desktop UI.

#### SharedPreferences

Flocon automatically detects and displays all standard `SharedPreferences` files in your application. However, you can manually register them to provide custom names:

```kotlin
val referencedPref = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
floconRegisterPreference(FloconSharedPreference(name = "User Settings", referencedPref))
```

#### Jetpack DataStore

To support Jetpack DataStore, ensure you have the `flocon-datastore` dependency:

```kotlin
// build.gradle.kts
debugImplementation("io.github.openflocon:flocon-datastores:version")
releaseImplementation("io.github.openflocon:flocon-datastores-no-op:version")
```

Then register your DataStore:

```kotlin
val Context.dataStore by preferencesDataStore(name = "settings")

// Registration
floconRegisterPreference(FloconDatastorePreference(name = "App Settings", context.dataStore))
```

#### Custom Implementations

You can expose any key-value store to Flocon by implementing the `FloconPreference` interface. This is useful for custom encrypted storage or non-standard persistence layers.

```kotlin
class MyCustomPreference : FloconPreference {
    override val name: String = "Custom Store"

    override suspend fun columns(): List<String> {
        return listOf("api_key", "debug_mode")
    }

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        // Retrieve value from your store
        return FloconPreferenceValue(stringValue = "...")
    }

    override suspend fun set(columnName: String, value: FloconPreferenceValue) {
        // Save value to your store
    }
}

// Registration
floconRegisterPreference(MyCustomPreference())
```