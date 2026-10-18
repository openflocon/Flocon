# Deeplinks

`plugin`: `Deeplink`

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
        { "type": "auto_complete", "name": "tab", "autoComplete": ["posts", "friends"] }
      ]
    }
  ],
  "variables": [
    { "name": "userId", "description": "Current user id" },
    { "name": "env", "mode": { "type": "auto_complete", "suggestions": ["prod", "staging"] } }
  ]
}
```
