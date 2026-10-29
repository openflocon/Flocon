# Dashboard

`plugin`: `dashboard`

| Direction        | `method`                 | Body                   |
|------------------|--------------------------|------------------------|
| Mobile → Desktop | `update`                 | `Dashboard`            |
| Desktop → Mobile | `on_click`                | raw button id (String) |
| Desktop → Mobile | `on_form_submitted`        | `FormSubmitted`        |
| Desktop → Mobile | `on_text_field_submitted`   | `TextFieldSubmitted`   |
| Desktop → Mobile | `on_check_box_value_changed` | `CheckBoxValueChanged` |

Interactive element ids sent by the mobile are `<dashboardId>_<elementId>`. The desktop sends them back as-is in the callbacks.

## `update` (Mobile → Desktop)

Sent each time a dashboard changes, and for every known dashboard on (re)connection. Replaces the whole dashboard.

### `Dashboard`

| Key           | Type              | Description               |
|---------------|-------------------|---------------------------|
| `dashboardId` | `String`          | Dashboard id              |
| `containers`  | `List<Container>` | Sections / forms, ordered |

### `Container`

| Key               | Type              | Description                     |
|-------------------|-------------------|---------------------------------|
| `name`            | `String`          | Container title                 |
| `elements`        | `List<Element>`   | Content, ordered                |
| `containerConfig` | `ContainerConfig` | `SECTION` or `FORM` config      |

### `ContainerConfig`

| Key             | Type     | Present for | Description                       |
|-----------------|----------|-------------|-----------------------------------|
| `containerType` | `String` | all         | `SECTION` or `FORM`               |
| `formId`        | `String` | `FORM`      | Id sent back in `on_form_submitted` |
| `submitText`    | `String` | `FORM`      | Submit button text                |

### `Element`

An `Element` is a JSON object with **exactly one key**, the element type:

| Key         | Value                                         |
|-------------|-----------------------------------------------|
| `button`    | `{ "id": String, "text": String }`            |
| `label`     | `{ "label": String, "color": Int? }`          |
| `text`      | `{ "label": String, "value": String, "color": Int? }` |
| `textField` | `{ "id": String, "label": String, "placeHolder": String?, "value": String }` |
| `checkBox`  | `{ "id": String, "label": String, "value": Boolean }` |
| `plainText` | `{ "label": String, "value": String, "type": "text" \| "json" }` |
| `markdown`  | `{ "label": String, "value": String }`        |
| `html`      | `{ "label": String, "value": String }`        |

`color` is an ARGB integer, omitted when not set. Inside a `FORM`, `textField` / `checkBox` values are collected by the desktop and sent with `on_form_submitted` (keyed by element `id`).

### Example

```json
{
  "dashboardId": "user",
  "containers": [
    {
      "name": "User",
      "elements": [
        { "text": { "label": "id", "value": "42" } },
        { "label": { "label": "Premium", "color": -16711936 } },
        { "button": { "id": "user_logout", "text": "Logout" } },
        { "checkBox": { "id": "user_debug", "label": "Debug mode", "value": false } },
        { "plainText": { "label": "Token", "value": "{\"exp\":1729260000}", "type": "json" } }
      ],
      "containerConfig": { "containerType": "SECTION" }
    },
    {
      "name": "Edit user",
      "elements": [
        { "textField": { "id": "user_name", "label": "Name", "placeHolder": "new name", "value": "Florent" } }
      ],
      "containerConfig": { "containerType": "FORM", "formId": "user_edit", "submitText": "Save" }
    }
  ]
}
```

## `on_click` (Desktop → Mobile)

Body is **not JSON**: it is directly the button `id`.

```
user_logout
```

## `on_form_submitted` (Desktop → Mobile)

### `FormSubmitted`

| Key      | Type                  | Description                     |
|----------|-----------------------|---------------------------------|
| `id`     | `String`              | `formId` of the form            |
| `values` | `Map<String, String>` | element `id` → submitted value  |

```json
{ "id": "user_edit", "values": { "user_name": "John" } }
```

## `on_text_field_submitted` (Desktop → Mobile)

### `TextFieldSubmitted`

| Key     | Type     | Description     |
|---------|----------|-----------------|
| `id`    | `String` | Text field `id` |
| `value` | `String` | Submitted text  |

```json
{ "id": "user_name", "value": "John" }
```

## `on_check_box_value_changed` (Desktop → Mobile)

### `CheckBoxValueChanged`

| Key     | Type      | Description   |
|---------|-----------|---------------|
| `id`    | `String`  | Checkbox `id` |
| `value` | `Boolean` | New value     |

```json
{ "id": "user_debug", "value": true }
```
