---
title: Preferences & DataStore
description: Inspect and edit SharedPreferences and Jetpack DataStore from your desktop.
---

# 🗝️ Preferences & DataStore Editor

Flocon provides full visibility into your app's key-value storage (`SharedPreferences` and `Jetpack DataStore`), allowing you to view and **edit values live** from the desktop UI.

---

## Overview

<img width="1295" height="836" alt="Preferences UI" src="https://github.com/user-attachments/assets/03c3278b-dc2f-4943-ba17-b18030e204ea" style="border-radius: 8px;" />

Features:
- **Auto-Discovery**: Automatically finds and lists default `SharedPreferences` files on Android.
- **DataStore Support**: Seamlessly browse and mutate `PreferencesDataStore` keys.
- **Live Mutation**: Edit booleans, numbers, strings, and JSON objects in real-time.

---

## Setup & Registration

### Standard SharedPreferences (Android)

Default SharedPreferences are automatically indexed. To provide a custom display name:

```kotlin
val userPrefs = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
floconRegisterPreference(FloconSharedPreference(name = "User Settings", userPrefs))
```

---

### Jetpack DataStore

=== "libs.versions.toml"

    ```toml
    [libraries]
    flocon-datastores = { module = "io.github.openflocon:flocon-datastores", version.ref = "flocon" }
    flocon-datastores-no-op = { module = "io.github.openflocon:flocon-datastores-no-op", version.ref = "flocon" }
    ```

=== "build.gradle.kts"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-datastores:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-datastores-no-op:LAST_VERSION")
    }
    ```

Register your DataStore instance:

```kotlin
val Context.dataStore by preferencesDataStore(name = "settings")

// Register with Flocon
floconRegisterPreference(FloconDatastorePreference(name = "App Settings", context.dataStore))
```

---

### Custom Key-Value Storage

You can expose any proprietary storage system (such as EncryptedSharedPreferences or custom key-value stores) by implementing `FloconPreference`:

```kotlin
class MyCustomPreference : FloconPreference {
    override val name: String = "Encrypted Vault"

    override suspend fun columns(): List<String> = listOf("token", "is_authenticated")

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        return FloconPreferenceValue(stringValue = readSecureKey(columnName))
    }

    override suspend fun set(columnName: String, value: FloconPreferenceValue) {
        writeSecureKey(columnName, value.stringValue)
    }
}

floconRegisterPreference(MyCustomPreference())
```