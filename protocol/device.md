# Device

`plugin`: `device`

| Direction        | `method`         | Body                     |
|------------------|------------------|--------------------------|
| Mobile → Desktop | `registerDevice` | `RegisterDevice`         |
| Mobile → Desktop | `appIcon`        | raw Base64 string        |
| Desktop → Mobile | `getAppIcon`     | `""` (empty)             |
| Desktop → Mobile | `restartApp`     | `""` (empty)             |

## `registerDevice` (Mobile → Desktop)

Android only. The desktop sends the adb serial to the app through an adb broadcast (`am broadcast ... --es serial <serial>`), the app sends it back so the desktop can map the websocket client to the adb device.

### `RegisterDevice`

| Key      | Type     | Description   |
|----------|----------|---------------|
| `serial` | `String` | Device serial |

```json
{ "serial": "emulator-5554" }
```

## `getAppIcon` (Desktop → Mobile) / `appIcon` (Mobile → Desktop)

The desktop asks for the application icon. The mobile answers with `appIcon`, whose `body` is **not JSON**: it is directly the Base64 encoded image (PNG).

```
iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAA...
```

No response if the icon can't be retrieved.

## `restartApp` (Desktop → Mobile)

Restarts the application. Empty body, no response.
