# Bluetooth transport (experimental)

Instead of the websocket (`9023`) and the http upload (`9024`), the app can talk to the desktop over one **Bluetooth RFCOMM** link (`FloconConnectionMode.Bluetooth`, Android only). The messages are the same ones as in the other files of this folder: only the way they are carried changes.

## Roles

| Side | Role |
|---|---|
| App (SDK) | RFCOMM **server**: `listenUsingRfcommWithServiceRecord("Flocon", b3c7e6a2-5f1d-4e8a-9a37-2f6d4c1e0a42)`, accepts one desktop at a time |
| OS of the desktop | Opens the RFCOMM connection to the phone and exposes it as a serial port (`/dev/rfcomm0`, a Windows outgoing COM port, `/dev/tty.<device>` on macOS) |
| Desktop (Bluetooth bridge) | Opens that serial port, forwards the frames to the Flocon server on `ws://localhost:9023/` and `http://localhost:9024/upload`, and the answers back |

The server does not know the app is on Bluetooth: for it, the bridge is one websocket client that sends the messages of the app.

## Frames

A serial / RFCOMM link is a byte stream, so every unit is framed:

```
[type: 1 byte][payload length: 4 bytes, big endian][payload]
```

| Type | Name | Direction | Payload |
|---|---|---|---|
| `1` | `Text` | both | A websocket text message (the JSON envelope of [README.md](README.md)), UTF-8 |
| `2` | `FileStart` | app → desktop | `transferId` (4 bytes, big endian) + metadata json (UTF-8), see below |
| `3` | `FileChunk` | app → desktop | `transferId` (4 bytes) + the next bytes of the file (the SDK sends 16 KiB at most) |
| `4` | `FileEnd` | app → desktop | `transferId` (4 bytes) |

- The payload of a frame is at most 32 MiB. A bigger size, an unknown type or a file frame without a `transferId` is a protocol error: the desktop closes the link.
- A frame can be split between two reads, and one read can carry several frames.
- Frames of different kinds can be interleaved: the SDK writes each frame on its own, so a message can go between two chunks of a file.

### `FileStart` metadata

The form fields of the `/upload` request (see [files.md](files.md)), as json:

| Key | Type | Description |
|---|---|---|
| `remotePath` | `String` | Path of the file on the device |
| `requestId` | `String` | Id of the request that asked for the file |
| `deviceId` | `String` | Same as the envelope |
| `appPackageName` | `String` | Same as the envelope |
| `appInstance` | `Long` | Same as the envelope |
| `fileName` | `String` | Name of the file, the desktop only keeps the last segment |

The desktop writes the chunks of a `transferId` to a temporary file and posts it to `/upload` on `FileEnd`.

## Limits

- One app at a time per phone (one service uuid).
- Throughput is about 1-2 Mbit/s, large payloads (images, database dumps) are slow.
- The permission `BLUETOOTH_CONNECT` must be granted to the app at runtime (API 31+), the SDK only declares it.

## Source of truth

The codec is duplicated, keep both identical on the wire:
- App: `FloconAndroid/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/bluetooth/BluetoothFrames.kt`
- Desktop: `FloconDesktop/data/remote/src/commonMain/kotlin/com/flocon/data/remote/bluetooth/BluetoothFrames.kt`
