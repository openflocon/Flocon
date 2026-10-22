# Bluetooth connection (exploration)

Exploration only, no code yet. It builds on [Connection modes](connection-modes.md).

## Why

- No USB / adb available.
- Wi-Fi unusable for `Url` mode: client isolation on corporate or guest Wi-Fi, no shared network, field testing.

## Constraint

The JVM has no Bluetooth API. BlueCove is abandoned and broken on modern macOS and Java 9+. The desktop side therefore always goes through the OS (network interface, serial port) or a native library.

## Option A: Bluetooth PAN tethering + `Url` mode

The phone shares its connection over Bluetooth (Android: Settings > Hotspot & tethering > Bluetooth tethering). The desktop joins the PAN and gets an ip (Android uses `192.168.44.0/24`, the phone is the gateway). That ip is site-local, so it already appears in Settings > Connection.

```kotlin
connectionMode = FloconConnectionMode.Url(host = "<desktop PAN ip>")
```

The app also needs the cleartext network config, as for any `Url` use.

| Aspect | Detail |
|---|---|
| Code | None, works with the current `Url` mode |
| Throughput | About 1-2 Mbit/s |
| Desktop OS | macOS, Windows, Linux (NetworkManager) support PAN |
| iOS | Personal Hotspot over Bluetooth requires a cellular plan |
| Risks | Some Android OEMs and carriers block tethering |

## Option B: RFCOMM / SPP stream

| Part | Design |
|---|---|
| Android SDK | Opens a `BluetoothServerSocket` with `listenUsingRfcommWithServiceRecord("Flocon", <fixed UUID>)`. The phone is the server because the desktop cannot easily host one. |
| Permission | `BLUETOOTH_CONNECT` runtime permission on API 31+. The SDK is a debug dependency so the manifest addition is acceptable, but the user is prompted. |
| Desktop link | Pair the phone. The OS exposes the RFCOMM channel as a serial port: Linux `rfcomm bind` / `/dev/rfcomm0`, Windows outgoing COM port, macOS `/dev/tty.*` (flaky for phones on recent macOS). Read it with jSerialComm. |
| Framing | A byte stream has no frame boundaries and no http upload. Use length-prefixed frames with a type byte: text message or file chunk. |
| Desktop bridge | A "Bluetooth bridge" opens the serial port and, per app, opens a websocket client to `ws://localhost:9023`, forwarding frames both ways. File chunks are re-assembled and POSTed to `localhost:9024/upload`. The server is untouched. |
| SDK abstraction | A `FloconTransport` (connect / send text / send file / receive), implemented by the current websocket + http client and by a new Bluetooth transport. Selected by a future `FloconConnectionMode.Bluetooth`. |
| Platforms | Android only (iOS does not expose RFCOMM outside MFi) |
| Throughput | About 1-2 Mbit/s in practice |
| Effort | Medium on the SDK, high on the desktop (per-OS port discovery, pairing UX) |

## Option C: BLE GATT

| Part | Design |
|---|---|
| Phone | Peripheral. Android `BluetoothLeAdvertiser` + `BluetoothGattServer`; iOS CoreBluetooth `CBPeripheralManager`. The only option that also covers physical iOS devices. |
| Desktop | No BLE stack on the JVM. Needs JNI libs such as SimpleBLE (SimpleJavaBLE), or BlueZ D-Bus on Linux only. |
| Throughput | Tens of KB/s even with a 512-byte MTU and 2M PHY. Needs chunking and flow control. |
| Impact | Heavy payloads (network bodies, images, database results, files) become slow. |
| Effort | High on all three sides (Android, iOS, desktop) |

## Comparison

| | A: PAN + `Url` | B: RFCOMM | C: BLE GATT |
|---|---|---|---|
| Code needed | None | `FloconTransport`, Bluetooth transport, desktop bridge | Same as B plus iOS, plus chunking and flow control |
| Android | Yes | Yes | Yes |
| iOS | Only with a cellular plan | No | Yes |
| Desktop OS | macOS, Windows, Linux | Per-OS serial port, macOS flaky | Native libs, Linux via BlueZ |
| Throughput | About 1-2 Mbit/s | About 1-2 Mbit/s | Tens of KB/s |
| Pairing / permissions UX | Pair + enable tethering | Pair + `BLUETOOTH_CONNECT` prompt | Pair/scan + permissions on both platforms |
| Files | Yes (http upload as is) | Yes, via chunk frames | Slow, via chunk frames |
| Effort | None | Medium SDK, high desktop | High everywhere |

## Recommendation

1. Document and validate **A** now. It needs no code and works with PR #670.
2. Prototype **B** behind the `FloconTransport` abstraction and the desktop bridge only if A proves insufficient.
3. Consider **C** only if iOS without a network becomes a need.

## Open questions

- Is Bluetooth worth it compared to `Url` + a VPN (Tailscale)?
- Who initiates pairing? Perhaps a desktop UI listing the paired devices.
- Several apps on one phone over one RFCOMM link: multiplex by `appInstance` in the frames?
- Security: Bluetooth pairing gives link encryption, unlike plain `ws` on a LAN.
