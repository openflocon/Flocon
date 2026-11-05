# Timeline

`plugin`: `timeline`

| Direction        | `method` | Body            |
|------------------|----------|-----------------|
| Mobile → Desktop | `log`    | `TimelineEvent` |

No message from desktop to mobile (the desktop is read only). One message per logged event, sent as soon as the app calls the SDK.

## `log` (Mobile → Desktop)

### `TimelineEvent`

| Key         | Type                  | Description                                                                 |
|-------------|-----------------------|-----------------------------------------------------------------------------|
| `type`      | `String`              | Type of the event, see [Types](#types)                                      |
| `value`     | `String`              | Main text shown in the row (ex: a screen name)                              |
| `params`    | `Map<String, String>` | Optional, text only. Omitted by the SDK when empty, read as empty when absent |
| `level`     | `String?`             | `debug`, `info`, `warning` or `error`. Optional, read as `info` when absent. An unknown value is read as `info` |
| `timestamp` | `Long`                | Moment of the call in the app (epoch ms), not the moment the message is sent |

### Types

The reserved types are `navigation`, `userAction` and `lifecycle`. The desktop matches them **case-insensitively** and shows them with their own icon. Any other value is a custom tag, kept as is: it appears as a value of the Type filter and uses a default style.

A log sent with a typed call (`log(type, ...)`, `logNavigation`, `logUserAction`, `logLifecycle`) has `type = type.name` and **no `level`**. A log sent with a tag (`log(tag, ..., level)`) has `type = tag` and a `level`. A tag equal to a reserved name is shown as that type.

### Compatibility

All the fields except `type`, `value` and `timestamp` are optional with a default, so an older SDK or desktop is never broken. The desktop keeps at most 20 000 logs per device and app, the oldest ones are dropped first.

### Example

```json
{
  "type": "navigation",
  "value": "CheckoutScreen",
  "params": { "from": "CartScreen" },
  "timestamp": 1729260012345
}
```

```json
{
  "type": "billing",
  "value": "Purchase failed",
  "params": { "sku": "premium_yearly" },
  "level": "error",
  "timestamp": 1729260015678
}
```
