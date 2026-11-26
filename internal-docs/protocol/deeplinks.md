# Deeplinks

`plugin`: `deeplink`

| Direction        | `method`       | Body        |
|------------------|----------------|-------------|
| Mobile → Desktop | `GetDeeplinks` | `Deeplinks` |

No message from desktop to mobile (deeplinks are launched by the desktop through adb). Sent on registration and on each (re)connection.

### `Deeplinks`

| Key         | Type                     | Description                       |
|-------------|--------------------------|-----------------------------------|
| `deeplinks` | `List<Deeplink>`         | Available deeplinks               |
| `variables` | `List<DeeplinkVariable>` | Variables shared by all deeplinks |

### `Deeplink`

| Key           | Type                      | Description                                       |
|---------------|---------------------------|---------------------------------------------------|
| `link`        | `String`                  | Link template, ex: `app://user/[userId]`          |
| `label`       | `String?`                 | Display label                                     |
| `description` | `String?`                 | Description                                       |
| `parameters`  | `List<DeeplinkParameter>` | Parameters of the link (placeholders `[name]`)    |

### `DeeplinkParameter`

Polymorphic, discriminated by `type`:

| `type`          | Key            | Type           | Description                         |
|-----------------|----------------|----------------|-------------------------------------|
| `auto_complete` | `name`         | `String`       | Parameter name                      |
|                 | `autoComplete` | `List<String>` | Suggested values                    |
|                 | `suggestions`  | `List<DeeplinkSuggestion>?` | Same suggestions with a label and a default, see below |
| `variable`      | `name`         | `String`       | Parameter name                      |
|                 | `variableName` | `String`       | Name of a `DeeplinkVariable`        |

### `DeeplinkVariable`

| Key           | Type      | Description                                   |
|---------------|-----------|-----------------------------------------------|
| `name`        | `String`  | Variable name                                 |
| `description` | `String?` | Description                                   |
| `mode`        | `Mode?`   | Omitted = `input`                             |

### `Mode`

Polymorphic, discriminated by `type`:

| `type`          | Key           | Type           | Description          |
|-----------------|---------------|----------------|----------------------|
| `input`         | –             | –              | Free text input      |
| `auto_complete` | `suggestions` | `List<String>` | Suggested values     |
|                 | `items`       | `List<DeeplinkSuggestion>?` | Same suggestions with a label and a default, see below |

### `DeeplinkSuggestion`

| Key         | Type      | Description                                                              |
|-------------|-----------|--------------------------------------------------------------------------|
| `value`     | `String`  | What is inserted in the link                                             |
| `label`     | `String?` | What the desktop shows instead of the value                              |
| `isDefault` | `Boolean` | Omitted = `false`. Pre-selected while the field is empty (first one wins) |

Backward compatibility: `autoComplete` / `suggestions` always carry the plain values, so a desktop that does not know the detailed list keeps working. An SDK with labels sends both; the desktop uses the detailed list (`suggestions` of a parameter, `items` of a variable) when it is present and falls back to the plain values otherwise.

### Example

```json
{
  "deeplinks": [
    {
      "label": "Home",
      "link": "app://home",
      "parameters": []
    },
    {
      "label": "User",
      "link": "app://user/[userId]?tab=[tab]",
      "description": "Open a user profile",
      "parameters": [
        { "type": "variable", "name": "userId", "variableName": "userId" },
        {
          "type": "auto_complete",
          "name": "tab",
          "autoComplete": ["posts", "friends"],
          "suggestions": [
            { "value": "posts", "isDefault": true },
            { "value": "friends", "label": "Friends list" }
          ]
        }
      ]
    }
  ],
  "variables": [
    { "name": "userId", "description": "Current user id" },
    {
      "name": "env",
      "mode": {
        "type": "auto_complete",
        "suggestions": ["prod", "staging"],
        "items": [
          { "value": "prod", "label": "Production" },
          { "value": "staging", "label": "Staging", "isDefault": true }
        ]
      }
    }
  ]
}
```
