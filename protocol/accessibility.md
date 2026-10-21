# Accessibility

`plugin`: `Accessibility`

| Direction        | `method`    | Body                       |
|------------------|-------------|----------------------------|
| Mobile → Desktop | `AddEvents` | `List<AccessibilityItem>`  |
| Mobile → Desktop | `State`     | `AccessibilityStateItem`   |

No message from desktop to mobile.

## `AddEvents` (Mobile → Desktop)

Body is a **JSON array** of `AccessibilityItem`. The SDK sends one message per event (array of size 1), but the desktop accepts any size.

### `AccessibilityItem`

| Key                  | Type                  | Description                                                                                          |
|----------------------|-----------------------|------------------------------------------------------------------------------------------------------|
| `id`                 | `String`              | Unique event id                                                                                      |
| `type`               | `String`              | Kind of event: `VIEW_FOCUSED`, `ELEMENT_FOCUSED`, `ANNOUNCEMENT`, `VIEW_CLICKED`, `CLICK`, `WINDOW_STATE_CHANGED`... Free string |
| `text`               | `String?`             | Text read by the screen reader                                                                       |
| `contentDescription` | `String?`             | Content description (Android) or accessibility label (iOS) of the element                            |
| `className`          | `String?`             | Class of the element                                                                                 |
| `screen`             | `String?`             | Screen the event comes from (activity class, package...)                                             |
| `bounds`             | `String?`             | Bounds of the element on screen, ex: `[0,0][100,50]`                                                 |
| `extras`             | `Map<String, String>` | Platform specific details (hint, traits, assistive technology...). Omitted when empty                |
| `createdAt`          | `Long`                | Event timestamp (ms)                                                                                 |

### `CLICK` events

Sent by Android for each tap on a clickable element, even when no accessibility service is running. `text`, `contentDescription` and `className` (View class, or Compose role such as `Button`) describe what a screen reader would read. `extras` can contain:

| Key | Description |
|---|---|
| `source` | `view` or `compose` |
| `viewId` | Resource entry name (Views) |
| `testTag`, `stateDescription`, `clickLabel` | Compose semantics, when set |
| `enabled` | `true` / `false` |
| `missingLabel` | `"true"` when there is no text and no content description |

### Example

```json
[
  {
    "id": "6c1a2b0e-5c0d-4b5e-9f0a-1e2d3c4b5a69",
    "type": "VIEW_FOCUSED",
    "text": "Add to cart",
    "contentDescription": "Add to cart",
    "className": "android.widget.Button",
    "screen": "com.example.CartActivity",
    "bounds": "[48,1530][1032,1674]",
    "extras": { "packageName": "com.example" },
    "createdAt": 1729260000000
  }
]
```

## `State` (Mobile → Desktop)

Body is a single `AccessibilityStateItem`. The SDK sends it when the plugin is installed, again when the accessibility settings change, and again each time the app connects to the desktop.

### `AccessibilityStateItem`

| Key                | Type           | Description                                                                              |
|--------------------|----------------|------------------------------------------------------------------------------------------|
| `supported`        | `Boolean`      | `false` when the platform cannot capture accessibility events automatically (JVM, Wasm)  |
| `enabled`          | `Boolean`      | `true` when an assistive technology is on (TalkBack, VoiceOver, Switch Control...)       |
| `touchExploration` | `Boolean`      | `true` when touch exploration is on (TalkBack explore by touch, VoiceOver)               |
| `services`         | `List<String>` | Enabled accessibility services (Android service ids, `VoiceOver`, `SwitchControl` on iOS) |

### Example

```json
{
  "supported": true,
  "enabled": true,
  "touchExploration": true,
  "services": ["com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService"]
}
```
