# Feature Flags

`plugin`: `feature_flags`

| Direction        | `method`   | Body           |
|------------------|------------|----------------|
| Mobile → Desktop | `register` | `FeatureFlags` |

No message from desktop to mobile (the desktop is read only). Sent on registration and on each (re)connection, one message per provider.

## `register` (Mobile → Desktop)

The body **replaces** all the flags previously sent for this `provider`. Flags sent by a previous app session (`appInstance`) are dropped.

### `FeatureFlags`

| Key        | Type                | Description                                        |
|------------|---------------------|----------------------------------------------------|
| `provider` | `String`            | Provider of the flags (ex: `firebase`, `statsig`)  |
| `flags`    | `List<FeatureFlag>` | All the flags of this provider                     |

### `FeatureFlag`

| Key       | Type      | Description                                                  |
|-----------|-----------|--------------------------------------------------------------|
| `name`    | `String`  | Flag key                                                     |
| `state`   | `String`  | `on`, `off`, or `custom` (an experiment arm, a value...). An unknown value is read as `custom` |
| `variant` | `String`  | Evaluated variant (ex: `control`, `treatment`, `on`)         |
| `payload` | `String?` | Optional payload attached to the variant, often a JSON string |

### Example

```json
{
  "provider": "firebase",
  "flags": [
    { "name": "new_checkout", "state": "custom", "variant": "treatment", "payload": "{\"buttonColor\":\"#FF5722\"}" },
    { "name": "dark_mode", "state": "on", "variant": "on" }
  ]
}
```
