# Device

`plugin`: `device`

| Direction        | `method`         | Body                     |
|------------------|------------------|--------------------------|
| Mobile → Desktop | `register_device` | `RegisterDevice`         |
| Mobile → Desktop | `app_icon`        | raw Base64 string        |
| Desktop → Mobile | `get_app_icon`     | `""` (empty)             |

These messages are handled by the SDK core: there is no `device` plugin to install. (`restart_app` existed in earlier versions and was removed; an SDK that still receives it ignores it.)

## `register_device` (Mobile → Desktop)

Android only. The desktop sends the adb serial to the app through an adb broadcast (`am broadcast ... --es serial <serial>`), the app sends it back so the desktop can map the websocket client to the adb device.

### `RegisterDevice`

| Key      | Type     | Description   |
|----------|----------|---------------|
| `serial` | `String` | Device serial |

```json
{ "serial": "emulator-5554" }
```

## `get_app_icon` (Desktop → Mobile) / `app_icon` (Mobile → Desktop)

The desktop asks for the application icon. The mobile answers with `app_icon`, whose `body` is **not JSON**: it is directly the Base64 encoded image (PNG).

```
iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAA...
```

No response if the icon can't be retrieved.
