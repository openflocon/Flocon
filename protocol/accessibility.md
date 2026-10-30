# Accessibility

`plugin`: `accessibility`

| Direction        | `method`    | Body                       |
|------------------|-------------|----------------------------|
| Mobile → Desktop | `AddEvents` | `List<AccessibilityItem>`  |
| Mobile → Desktop | `State`     | `AccessibilityStateItem`   |
| Mobile → Desktop | `FocusedNode` | `AccessibilityFocusedNodeItem` |
| Desktop → Mobile | `GetFocusedNode` | empty |
| Desktop → Mobile | `FocusNext`  | empty |
| Desktop → Mobile | `FocusPrevious` | empty |
| Desktop → Mobile | `EnableTalkBack` | empty |
| Desktop → Mobile | `DisableTalkBack` | empty |

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

### What a screen reader knows about the element (`extras`)

On Android, the events that come from an element (focus, `CLICK`...), the elements of the focus order and the focused element carry these keys in `extras`, each only when it tells something:

| Key | Description |
|---|---|
| `spoken` | What a screen reader would likely say: label, role, state, hint, then what a double tap does (ex: `Add to cart, Button, double tap to activate`). A reconstruction: the app cannot hear the screen reader, and its settings change what it says |
| `role` | How the kind of element is read: `Button`, `Check box`, `Switch`, `Edit box`... |
| `stateDescription` | State of the element: the state description of the app, or `checked` / `not checked` / `on` / `off` |
| `hint` | Hint of the element (Views) |
| `clickLabel` | What the click does, when the app says it |
| `clickable`, `selected`, `heading` | `"true"` when the element is |
| `enabled` | `true` / `false` |
| `lostFocus` | `"true"` on a `VIEW_ACCESSIBILITY_FOCUS_CLEARED` event: the other fields (text, class, bounds, extras) describe the element that had the focus, not one that has it now |
| `customActions` | Labels of the custom actions, separated by ` \| ` |

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
| `canToggleTalkBack` | `Boolean`     | `true` when the app can turn TalkBack on and off (Android, `WRITE_SECURE_SETTINGS` granted). Omitted (`false`) otherwise |

### Example

```json
{
  "supported": true,
  "enabled": true,
  "touchExploration": true,
  "services": ["com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService"]
}
```

## `FocusedNode` (Mobile → Desktop)

The element the screen reader is on. Sent each time the accessibility focus changes (the body is then `{}` when the focus is cleared), and as the reply to `GetFocusedNode`, `FocusNext` and `FocusPrevious`. The body is a single `AccessibilityFocusedNodeItem`; every key is optional.

### `AccessibilityFocusedNodeItem`

| Key                  | Type                  | Description                                                                                  |
|----------------------|-----------------------|----------------------------------------------------------------------------------------------|
| `found`              | `Boolean`             | `true` when an element has the accessibility focus. Omitted (`false`) otherwise              |
| `text`, `contentDescription`, `className`, `screen`, `bounds`, `extras` | | Same as in `AccessibilityItem`                                       |
| `index`              | `Int?`                | Position of the element in the focus order of the screen. Only in the replies to a request   |
| `count`              | `Int?`                | Number of elements in the focus order of the screen. Only in the replies to a request        |
| `hasNext`            | `Boolean`             | `true` when `FocusNext` has an element to go to. Omitted (`false`) otherwise                 |
| `hasPrevious`        | `Boolean`             | `true` when `FocusPrevious` has an element to go to. Omitted (`false`) otherwise             |

## `GetFocusedNode`, `FocusNext`, `FocusPrevious` (Desktop → Mobile)

Empty body. `FocusNext` / `FocusPrevious` give the accessibility focus to the next / previous element of the focus order of the resumed screen, without wrapping, then reply with `FocusedNode`. The SDK builds the focus order itself (Views, and the merged semantics tree of Compose), so it can differ slightly from TalkBack's. It only works while an assistive technology is on: without one there is no accessibility focus. Android only, other platforms reply with an empty `FocusedNode`.

## `EnableTalkBack`, `DisableTalkBack` (Desktop → Mobile)

Empty body. The SDK adds TalkBack to (or removes it from) the enabled accessibility services of the device, the other services are kept, then replies with a `State`. Android only, and only when `canToggleTalkBack` is `true`: the app needs the `WRITE_SECURE_SETTINGS` permission, which only adb can grant (`adb shell pm grant <package> android.permission.WRITE_SECURE_SETTINGS`). The `accessibility` module declares it in its manifest, the `accessibility-no-op` one does not. The desktop grants it with adb the first time. Other platforms ignore these messages.
