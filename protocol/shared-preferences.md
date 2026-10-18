# Shared Preferences

`plugin`: `sharedPreferences`

| Direction        | `method`                   | Body                         |
|------------------|----------------------------|------------------------------|
| Desktop → Mobile | `getSharedPreferences`     | `""` (empty)                 |
| Mobile → Desktop | `getSharedPreferences`     | `List<Preference>`           |
| Desktop → Mobile | `getSharedPreferenceValue` | `GetValuesRequest`           |
| Desktop → Mobile | `setSharedPreferenceValue` | `SetValueRequest`            |
| Mobile → Desktop | `getSharedPreferenceValue` | `ValuesResult`               |

## `getSharedPreferences`

Desktop → Mobile: empty body. Mobile → Desktop: list of preference files (also sent on connection and when a preference is registered), sorted by name.

### `Preference`

| Key    | Type     | Description               |
|--------|----------|---------------------------|
| `name` | `String` | Preference file / store name |

```json
[
  { "name": "com.example.app_preferences" },
  { "name": "session" }
]
```

## `getSharedPreferenceValue` (Desktop → Mobile)

### `GetValuesRequest`

| Key                    | Type     | Description      |
|------------------------|----------|------------------|
| `requestId`            | `String` | Request id       |
| `sharedPreferenceName` | `String` | Preference `name` |

```json
{ "requestId": "9a7c…", "sharedPreferenceName": "session" }
```

## `setSharedPreferenceValue` (Desktop → Mobile)

Updates one key, then the mobile answers with `getSharedPreferenceValue` (full content). Exactly one `*Value` key should be set.

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

## `getSharedPreferenceValue` (Mobile → Desktop)

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
