# Tables

`plugin`: `table`

| Direction        | `method`   | Body              |
|------------------|------------|-------------------|
| Mobile → Desktop | `AddItems` | `List<TableItem>` |

No message from desktop to mobile.

## `AddItems` (Mobile → Desktop)

Body is a **JSON array** of `TableItem` (each item = one row). The SDK currently sends one row per message (array of size 1).

### `TableItem`

| Key         | Type                | Description                         |
|-------------|---------------------|-------------------------------------|
| `id`        | `String`            | Unique row id                       |
| `name`      | `String`            | Table name (rows are grouped by it) |
| `createdAt` | `Long`              | Row timestamp (ms)                  |
| `columns`   | `List<TableColumn>` | Cells of the row                    |

### `TableColumn`

| Key      | Type     | Description |
|----------|----------|-------------|
| `column` | `String` | Column name |
| `value`  | `String` | Cell value  |

### Example

```json
[
  {
    "id": "a7b0e2a4-2f7e-4f43-9a7b-1b3a1b6e0d11",
    "name": "users",
    "createdAt": 1729260000000,
    "columns": [
      { "column": "id", "value": "42" },
      { "column": "name", "value": "Florent" },
      { "column": "premium", "value": "true" }
    ]
  }
]
```
