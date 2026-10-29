# Crash Reporter

`plugin`: `crash_reporter`

| Direction        | `method`      | Body                |
|------------------|---------------|---------------------|
| Mobile → Desktop | `report_crash` | `List<CrashReport>` |

No message from desktop to mobile.

Crashes are stored locally on the device when they happen, then sent on the next connection to the desktop (and deleted from the device).

## `report_crash` (Mobile → Desktop)

Body is a **JSON array** of `CrashReport`.

### `CrashReport`

| Key                | Type     | Description                            |
|--------------------|----------|----------------------------------------|
| `crashId`          | `String` | Unique crash id (UUID)                 |
| `timestamp`        | `Long`   | Crash timestamp (ms)                   |
| `exceptionType`    | `String` | Exception class simple name            |
| `exceptionMessage` | `String` | Exception message (`No message` if none) |
| `stackTrace`       | `String` | Full stack trace                       |

### Example

```json
[
  {
    "crashId": "0d5e4c1f-8f3a-4f0e-b1c2-9a8b7c6d5e4f",
    "timestamp": 1729260000000,
    "exceptionType": "IllegalStateException",
    "exceptionMessage": "User must be logged in",
    "stackTrace": "java.lang.IllegalStateException: User must be logged in\n\tat com.example.MainActivity.onCreate(MainActivity.kt:42)\n\t..."
  }
]
```
