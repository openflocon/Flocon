# Shared Preferences

`plugin`: `shared_preferences`

| Direction        | `method`                   | Body                         |
|------------------|----------------------------|------------------------------|
| Desktop → Mobile | `get_shared_preferences`     | `""` (empty)                 |
| Mobile → Desktop | `get_shared_preferences`     | `List<Preference>`           |
| Desktop → Mobile | `get_shared_preference_value` | `GetValuesRequest`           |
| Desktop → Mobile | `set_shared_preference_value` | `SetValueRequest`            |
| Desktop → Mobile | `remove_shared_preference_value` | `RemoveValueRequest`      |
| Mobile → Desktop | `get_shared_preference_value` | `ValuesResult`               |

## `get_shared_preferences`

Desktop → Mobile: empty body. Mobile → Desktop: list of preference files (also sent on connection and when a preference is registered), sorted by name.

### `Preference`

| Key    | Type      | Description                                                                                             |
|--------|-----------|---------------------------------------------------------------------------------------------------------|
| `name` | `String`  | Preference file / store name                                                                            |
| `type` | `String?` | Storage, displayed by the desktop: `SharedPreferences`, `DataStore`, `UserDefaults`, `Keychain`, `Java Preferences`, `LocalStorage`, `Custom` or any free value. Absent for older SDKs |

```json
[
  { "name": "com.example.app_preferences", "type": "SharedPreferences" },
  { "name": "session", "type": "DataStore" }
]
```

## `get_shared_preference_value` (Desktop → Mobile)

### `GetValuesRequest`

| Key                    | Type     | Description      |
|------------------------|----------|------------------|
| `requestId`            | `String` | Request id       |
| `sharedPreferenceName` | `String` | Preference `name` |

```json
{ "requestId": "9a7c…", "sharedPreferenceName": "session" }
```

## `set_shared_preference_value` (Desktop → Mobile)

Updates one key, then the mobile answers with `get_shared_preference_value` (full content). Exactly one `*Value` key should be set.

### `SetValueRequest`

| Key                    | Type            | Description           |
|------------------------|-----------------|-----------------------|
| `requestId`            | `String`        | Request id            |
| `sharedPreferenceName` | `String`        | Preference `name`     |
| `key`                  | `String`        | Key to update         |
| `stringValue`          | `String?`       |                       |
| `intValue`             | `Int?`          |                       |
| `longValue`            | `Long?`         |                       |
| `floatValue`           | `Float?`        |                       |
| `booleanValue`         | `Boolean?`      |                       |
| `setStringValue`       | `List<String>?` | String set            |

```json
{ "requestId": "9a7c…", "sharedPreferenceName": "session", "key": "isLogged", "booleanValue": false }
```

## `remove_shared_preference_value` (Desktop → Mobile)

Removes one key, then the mobile answers with `get_shared_preference_value` (full content). Older SDKs ignore this method.

### `RemoveValueRequest`

| Key                    | Type     | Description       |
|------------------------|----------|-------------------|
| `requestId`            | `String` | Request id        |
| `sharedPreferenceName` | `String` | Preference `name` |
| `key`                  | `String` | Key to remove     |

```json
{ "requestId": "9a7c…", "sharedPreferenceName": "session", "key": "token" }
```

## `get_shared_preference_value` (Mobile → Desktop)

### `ValuesResult`

| Key                    | Type                  | Description        |
|------------------------|-----------------------|--------------------|
| `requestId`            | `String`              | Same as the request |
| `sharedPreferenceName` | `String`              | Preference `name`  |
| `rows`                 | `List<PreferenceRow>` | All keys / values  |

### `PreferenceRow`

Same value keys as `SetValueRequest`; only the one matching the stored type is present.

| Key              | Type            |
|------------------|-----------------|
| `key`            | `String`        |
| `stringValue`    | `String?`       |
| `intValue`       | `Int?`          |
| `longValue`      | `Long?`         |
| `floatValue`     | `Float?`        |
| `booleanValue`   | `Boolean?`      |
| `setStringValue` | `List<String>?` |

```json
{
  "requestId": "9a7c…",
  "sharedPreferenceName": "session",
  "rows": [
    { "key": "token", "stringValue": "eyJhbGciOi…" },
    { "key": "userId", "longValue": 42 },
    { "key": "isLogged", "booleanValue": true },
    { "key": "tags", "setStringValue": ["beta", "premium"] }
  ]
}
```
