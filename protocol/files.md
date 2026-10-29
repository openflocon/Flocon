# Files

`plugin`: `files`

| Direction        | `method`              | Body                   |
|------------------|-----------------------|------------------------|
| Desktop → Mobile | `get_roots`            | `GetRootsRequest`      |
| Desktop → Mobile | `list_files`           | `ListFilesRequest`     |
| Desktop → Mobile | `delete_file`          | `DeleteFileRequest`    |
| Desktop → Mobile | `delete_files`         | `DeleteFilesRequest`   |
| Desktop → Mobile | `delete_folder_content` | `DeleteFolderContentRequest` |
| Desktop → Mobile | `get_file`             | `GetFileRequest`       |
| Mobile → Desktop | `get_roots`            | `RootsResult`          |
| Mobile → Desktop | `list_files`           | `FilesResult`          |
| Mobile → Desktop | HTTP `POST /upload`   | multipart (`get_file` response) |

### Root folders

The mobile only gives access to its **root folders**: every path listed, downloaded or deleted must be a root folder or inside one
(symbolic links are resolved, `..` cannot leave a root). A root folder itself is never deleted, only its content.

The roots are the default folders of the platform, then the folders registered by the app (see [`get_roots`](#get_roots-desktop-mobile)).

### Constant paths

When `isConstantPath` / `isConstantParentPath` is `true`, the path is the `id` of a root folder. Otherwise it is an absolute path on the device.

| `id`                 | Android            | iOS                   | JVM                |
|----------------------|--------------------|-----------------------|--------------------|
| `files`              | `context.filesDir` | Documents             | `user.dir`         |
| `caches`             | `context.cacheDir` | Caches                | `java.io.tmpdir`   |
| `databases`          | databases folder   |                       |                    |
| `applicationSupport` |                    | Application Support   |                    |
| `tmp`                |                    | `NSTemporaryDirectory()` |                 |
| `container`          |                    | `NSHomeDirectory()`   |                    |
| `home`               |                    |                       | `user.home`        |

A registered folder has its absolute path as `id`. `files` and `caches` exist on every platform with a file system,
so a desktop that does not send `get_roots` keeps working.

## `get_roots` (Desktop → Mobile)

### `GetRootsRequest`

| Key         | Type     | Description |
|-------------|----------|-------------|
| `requestId` | `String` | Request id  |

```json
{ "requestId": "a7f3…" }
```

## `get_roots` (Mobile → Desktop)

Response to `get_roots`. Only the roots that exist as folders are sent. An older SDK does not answer it.

### `RootsResult`

| Key         | Type         | Description         |
|-------------|--------------|---------------------|
| `requestId` | `String`     | Same as the request |
| `roots`     | `List<Root>` | Root folders, default ones first |

### `Root`

| Key    | Type     | Description                                                      |
|--------|----------|------------------------------------------------------------------|
| `id`   | `String` | Sent back as the path when `isConstantPath` is `true`            |
| `name` | `String` | Name shown in the desktop app                                    |
| `path` | `String` | Absolute path of the folder                                      |

```json
{
  "requestId": "a7f3…",
  "roots": [
    { "id": "files", "name": "Files", "path": "/data/user/0/com.example.app/files" },
    { "id": "caches", "name": "Caches", "path": "/data/user/0/com.example.app/cache" },
    { "id": "databases", "name": "Databases", "path": "/data/user/0/com.example.app/databases" },
    { "id": "/data/user/0/com.example.app/shared_prefs", "name": "Shared preferences", "path": "/data/user/0/com.example.app/shared_prefs" }
  ]
}
```

## `list_files` (Desktop → Mobile)

### `ListFilesRequest`

| Key               | Type      | Description                               |
|-------------------|-----------|-------------------------------------------|
| `requestId`       | `String`  | Request id                                |
| `path`            | `String`  | Folder to list                            |
| `isConstantPath`  | `Boolean` | See [Constant paths](#constant-paths)     |
| `withFoldersSize` | `Boolean` | Compute folder sizes (default `false`)    |

```json
{ "requestId": "b1e9…", "path": "files", "isConstantPath": true, "withFoldersSize": true }
```

## `delete_file` (Desktop → Mobile)

### `DeleteFileRequest`

| Key                    | Type      | Description                                    |
|------------------------|-----------|------------------------------------------------|
| `requestId`            | `String`  | Request id                                     |
| `parentPath`           | `String`  | Folder listed again after deletion             |
| `filePath`             | `String`  | Absolute path of the file to delete (inside a root, not a root) |
| `isConstantParentPath` | `Boolean` | Applies to `parentPath`                        |

```json
{
  "requestId": "b1e9…",
  "parentPath": "files",
  "filePath": "/data/user/0/com.example.app/files/log.txt",
  "isConstantParentPath": true
}
```

## `delete_files` (Desktop → Mobile)

### `DeleteFilesRequest`

| Key                    | Type           | Description                          |
|------------------------|----------------|--------------------------------------|
| `requestId`            | `String`       | Request id                           |
| `parentPath`           | `String`       | Folder listed again after deletion   |
| `filePaths`            | `List<String>` | Absolute paths of files to delete    |
| `isConstantParentPath` | `Boolean`      | Applies to `parentPath`              |

```json
{
  "requestId": "b1e9…",
  "parentPath": "/data/user/0/com.example.app/files/logs",
  "filePaths": [
    "/data/user/0/com.example.app/files/logs/1.txt",
    "/data/user/0/com.example.app/files/logs/2.txt"
  ],
  "isConstantParentPath": false
}
```

## `delete_folder_content` (Desktop → Mobile)

### `DeleteFolderContentRequest`

| Key              | Type      | Description                            |
|------------------|-----------|----------------------------------------|
| `requestId`      | `String`  | Request id                             |
| `path`           | `String`  | Folder to empty (then listed again)    |
| `isConstantPath` | `Boolean` | See [Constant paths](#constant-paths)  |

```json
{ "requestId": "b1e9…", "path": "caches", "isConstantPath": true }
```

## `list_files` (Mobile → Desktop)

Response to `list_files`, `delete_file`, `delete_files` and `delete_folder_content` (content of the folder after the operation).

### `FilesResult`

| Key         | Type         | Description         |
|-------------|--------------|---------------------|
| `requestId` | `String`     | Same as the request |
| `files`     | `List<File>` | Folder content      |

### `File`

| Key            | Type      | Description                                   |
|----------------|-----------|-----------------------------------------------|
| `name`         | `String`  | File name                                     |
| `path`         | `String`  | Absolute path                                 |
| `isDirectory`  | `Boolean` | `true` for folders                            |
| `size`         | `Long`    | Size in bytes (folders: `0` unless `withFoldersSize`) |
| `lastModified` | `Long`    | Last modification timestamp (ms)              |

```json
{
  "requestId": "b1e9…",
  "files": [
    {
      "name": "logs",
      "isDirectory": true,
      "path": "/data/user/0/com.example.app/files/logs",
      "size": 20480,
      "lastModified": 1729260000000
    },
    {
      "name": "config.json",
      "isDirectory": false,
      "path": "/data/user/0/com.example.app/files/config.json",
      "size": 512,
      "lastModified": 1729250000000
    }
  ]
}
```

## `get_file` (Desktop → Mobile)

### `GetFileRequest`

| Key         | Type     | Description                       |
|-------------|----------|-----------------------------------|
| `requestId` | `String` | Request id                        |
| `path`      | `String` | Absolute path of the file         |

```json
{ "requestId": "c4d2…", "path": "/data/user/0/com.example.app/files/config.json" }
```

### Response: HTTP upload (Mobile → Desktop)

The file is **not** sent through the websocket but uploaded with `POST https://<host>:9024/upload` (`multipart/form-data`, header `User-Agent: Flocon`).

| Part             | Type        | Description                         |
|------------------|-------------|-------------------------------------|
| `remotePath`     | form field  | `path` from the request             |
| `requestId`      | form field  | `requestId` from the request        |
| `deviceId`       | form field  | Same as the websocket envelope      |
| `appPackageName` | form field  | Same as the websocket envelope      |
| `appInstance`    | form field  | Same as the websocket envelope      |
| `file`           | file        | File content (`application/octet-stream`) |

```bash
curl -k -X POST https://localhost:9024/upload \
  -H "User-Agent: Flocon" \
  -F remotePath=/data/user/0/com.example.app/files/config.json \
  -F requestId=c4d2… \
  -F deviceId=emulator-5554 \
  -F appPackageName=com.example.app \
  -F appInstance=1729260000000 \
  -F file=@config.json
```
