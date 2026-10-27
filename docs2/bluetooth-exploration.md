# Bluetooth connection (exploration)

Exploration. Option A needs no code, option B has a prototype (`FloconConnectionMode.Bluetooth`, see [Connection modes](connection-modes.md)). It builds on [Connection modes](connection-modes.md).

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

### Trying option A

Status: **checked in code, not yet tried on hardware** (the authoring environment has no Bluetooth).

What the code check established:

| Check | Result |
|---|---|
| The PAN address shows up in Settings > Connection | Yes. `192.168.44.x` is site-local (RFC 1918) and the interface filter only drops down, loopback and virtual interfaces. Covered by `LocalNetworkAddressesTest`. |
| The server accepts the connection | Yes. Both servers bind `0.0.0.0`, so any interface works. |
| The SDK reaches it | Yes, `Url(host = <PAN ip>)`. Nothing is resolved through adb in this mode. |
| The list is refreshed when the PAN comes up | **No.** The addresses are read once when the Settings screen opens. Join the PAN first, then open Settings (or reopen it). A refresh would be a small follow-up. |
| Cleartext `ws://` to that ip | Needs the network security config described in [Connection modes](connection-modes.md). |

To try it on real hardware:

1. Phone: pair with the desktop, then enable Settings > Hotspot & tethering > Bluetooth tethering.
2. Desktop: join the phone's Bluetooth network.
   - macOS: System Settings > Bluetooth, connect to the phone, then *Connect to Network*.
   - Windows: *Bluetooth & devices* > the phone > *Connect using* > *Access point*.
   - Linux: NetworkManager's Bluetooth connection (`bnep0` interface).
3. Open Flocon Desktop > Settings > Adb > Connection and note the `192.168.44.x` address.
4. App: `connectionMode = FloconConnectionMode.Url(host = "<that ip>")`, with cleartext allowed for it.
5. Allow ports `9023` / `9024` in the desktop firewall, unplug USB (or `adb kill-server`), and check the device and a network call show up in Flocon.
6. Note the latency and the time to open a large payload (an image or a database dump).

Results to report back here: OS pair used, whether step 3 showed the ip, and the throughput feel.

## Option B: RFCOMM / SPP stream

| Part | Design |
|---|---|
| Android SDK | Opens a `BluetoothServerSocket` with `listenUsingRfcommWithServiceRecord("Flocon", <fixed UUID>)`. The phone is the server because the desktop cannot easily host one. |
| Permission | `BLUETOOTH_CONNECT` runtime permission on API 31+. The SDK is a debug dependency so the manifest addition is acceptable, but the user is prompted. |
| Desktop link | Pair the phone. The OS exposes the RFCOMM channel as a serial port: Linux `rfcomm bind` / `/dev/rfcomm0`, Windows outgoing COM port, macOS `/dev/tty.*` (flaky for phones on recent macOS). Read it with jSerialComm. |
| Framing | A byte stream has no frame boundaries and no http upload. Use length-prefixed frames with a type byte: text message or file chunk. |
| Desktop bridge | A "Bluetooth bridge" opens the serial port and, per app, opens a websocket client to `ws://localhost:9023`, forwarding frames both ways. File chunks are re-assembled and POSTed to `localhost:9024/upload`. The server is untouched. |
| SDK abstraction | The existing websocket and http client interfaces implemented by one Bluetooth transport, selected by `FloconConnectionMode.Bluetooth` (built in the prototype, instead of a new `FloconTransport`). |
| Platforms | Android only (iOS does not expose RFCOMM outside MFi) |
| Throughput | About 1-2 Mbit/s in practice |
| Effort | Medium on the SDK, high on the desktop (per-OS port discovery, pairing UX) |

### Prototype of option B

Status: **built, the logic is tested, not yet tried with a real phone**.

| Part | What exists |
|---|---|
| SDK | `FloconConnectionMode.Bluetooth`. In `FloconClient`, a `FloconBluetoothTransport` replaces the websocket and the http clients (one RFCOMM server, Android only). The framing is in `BluetoothFrames.kt`, specified in [`protocol/transport-bluetooth.md`](https://github.com/openflocon/Flocon/blob/main/protocol/transport-bluetooth.md). |
| Desktop | `BluetoothBridge` reads a serial port and forwards to the unchanged server (websocket client for the messages, multipart post for the files). Settings > Connection > Bluetooth lists the serial ports that look like a phone, with Connect / Disconnect. Ports are listed with jSerialComm. |
| Sample | `SampleConnectionMode` in the Android sample `MainActivity`, and the runtime permission request. |

What was verified without a phone:

| Check | How |
|---|---|
| Framing (split reads, several frames per read, invalid input) | 8 unit tests, in the SDK and in the desktop module |
| The bridge | 5 tests against a real Ktor server: text both ways, a chunked file arriving on `/upload` with the right fields, the link closing, an invalid stream, an unreachable server |
| Serial port detection | Unit tests of the name heuristics (Linux, Windows, macOS) |

What is **not** verified: the Android side (`BluetoothServerSocket`) and the OS side (does the serial port appear and connect?) on real hardware, and the Settings UI in the running app.

Known limits:

- One app at a time per phone (one service uuid). Several apps would need a multiplexing id in the frames.
- The desktop depends on the OS to open the RFCOMM connection: `rfcomm` on Linux, an outgoing COM port on Windows, and on macOS a serial port only appears for some devices.
- The ports are listed when the Settings screen opens or on *Refresh*.
- No reconnection on the desktop: if the link drops, press Connect again. The app listens again by itself.

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
| Code needed | None | Bluetooth transport, desktop bridge | Same as B plus iOS, plus chunking and flow control |
| Android | Yes | Yes | Yes |
| iOS | Only with a cellular plan | No | Yes |
| Desktop OS | macOS, Windows, Linux | Per-OS serial port, macOS flaky | Native libs, Linux via BlueZ |
| Throughput | About 1-2 Mbit/s | About 1-2 Mbit/s | Tens of KB/s |
| Pairing / permissions UX | Pair + enable tethering | Pair + `BLUETOOTH_CONNECT` prompt | Pair/scan + permissions on both platforms |
| Files | Yes (http upload as is) | Yes, via chunk frames | Slow, via chunk frames |
| Effort | None | Medium SDK, high desktop | High everywhere |

## Recommendation

1. Document and validate **A** now. It needs no code and works with PR #670.
2. **B** is prototyped: try it with a phone, and keep it only if A proves insufficient or too manual.
3. Consider **C** only if iOS without a network becomes a need.

## Open questions

- Is Bluetooth worth it compared to `Url` + a VPN (Tailscale)?
- Who initiates pairing? Perhaps a desktop UI listing the paired devices.
- Several apps on one phone over one RFCOMM link: multiplex by `appInstance` in the frames?
- Security: Bluetooth pairing gives link encryption, unlike plain `ws` on a LAN.
