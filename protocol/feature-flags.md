# Feature Flags

`plugin`: `featureflags`

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
| `variant` | `String`  | Evaluated variant (ex: `control`, `treatment`, `on`)         |
| `payload` | `String?` | Optional payload attached to the variant, often a JSON string |

### Example

```json
{
  "provider": "firebase",
  "flags": [
    { "name": "new_checkout", "variant": "treatment", "payload": "{\"buttonColor\":\"#FF5722\"}" },
    { "name": "dark_mode", "variant": "on" }
  ]
}
```
