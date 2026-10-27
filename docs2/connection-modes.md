# Connection modes

The SDK can reach the Flocon desktop in three ways. You pick one per app with `connectionMode`:

| Mode | How the app dials the desktop | Needs |
|---|---|---|
| `FloconConnectionMode.Adb` (default) | `localhost` through `adb reverse` (`10.0.2.2` on an Android emulator) | A USB / adb link to the device |
| `FloconConnectionMode.Url(host)` | Directly to `host` (a LAN ip, a VPN ip, a public ip or a domain) | Network access to the desktop |
| `FloconConnectionMode.Bluetooth` (experimental, Android) | The app waits, the desktop connects over Bluetooth RFCOMM | A paired phone, see [Over Bluetooth](#over-bluetooth) |

The desktop listens on **both modes at the same time**: its websocket (`9023`) and http (`9024`) servers are bound on all interfaces, and it keeps running the `adb reverse` loop. There is nothing to switch on the desktop. A USB phone (`Adb`) and a Wi-Fi phone or an iOS device (`Url`) can show up side by side.

## Using the `Url` mode

```kotlin
startFlocon(FloconContext(this)) {
    connectionMode = FloconConnectionMode.Url(host = "192.168.1.20")
    install(FloconNetwork)
}
```

`websocketPort` and `httpPort` default to `9023` and `9024`. Override them only if you changed the desktop side.

The desktop lists its local addresses in **Settings > Adb** (section *Connection*), with a copy button for each `ws://<ip>:9023` url. Use the ip of the interface that your device can reach.

### Connecting later (QR code, typed ip)

If the host is only known after the app started (a QR code scanned, an ip typed in a debug screen), keep `startFlocon` at app start so that nothing is missed, and set the mode once you have the host:

```kotlin
// at app start: plugins are installed and capture right away
startFlocon(FloconContext(this)) {
    install(FloconNetwork)
}

// later, e.g. after scanning the QR code
Flocon.instance.setConnectionMode(FloconConnectionMode.Url(host = scannedHost))
```

Until a desktop is reachable, the SDK retries every 3 seconds and keeps the events in a queue, which are sent once connected. The queue holds the **200 most recent messages**: if the app is very chatty before the host is set, the oldest ones are dropped.

`setConnectionMode` can also be called while connected, to move the app to another desktop: the current session is closed and the SDK reconnects to the new host. It is a no-op in the `flocon-no-op` artifact.

### Android: allow cleartext traffic

The SDK uses `ws://` and `http://`. Android blocks cleartext traffic to anything but `localhost` by default (API 28+), so add a network security config to your debug build:

```xml
<!-- res/xml/network_rules.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">192.168.1.20</domain>
    </domain-config>
</network-security-config>
```

```xml
<application android:networkSecurityConfig="@xml/network_rules" ... />
```

The sample app (`FloconAndroid/sample/android`) allows cleartext globally, which is fine for a sample but not for your app. iOS may also require an App Transport Security exception for local networking.

### Firewall

Allow incoming TCP connections on ports `9023` and `9024` on the desktop. The device and the desktop must be on the same network, or reach each other through a VPN.

### What needs adb

`Url` replaces the transport used by the plugins. These features run `adb` commands from the desktop and still need an adb link to the device:

- screenshots and screen recordings
- ADB Commander
- restarting the app from the desktop

Everything else (network, databases, preferences, dashboards, analytics, ...) works without adb.

## Public IP

`Url` accepts any host, so a public ip or a domain works as is. The network side is yours to set up:

- **Recommended:** a VPN overlay such as Tailscale or ZeroTier. Use the desktop's VPN ip as `host`. It is reachable from anywhere without being exposed to the internet.
- Forward ports `9023` and `9024` from your router to the desktop, and use the public ip or domain as `host`.

**Warning: the Flocon server has no authentication and no TLS. Anyone who can reach the port can impersonate an app, and can receive and drive the desktop's commands (database queries, preferences edits, ...). Never expose the ports to the open internet.**

TLS tunnels (ngrok, cloudflared, ...) terminate on `wss://` / `https://`, which the SDK does not support yet: its schemes are fixed to `ws` and `http`.

## Over Bluetooth

No USB and no usable Wi-Fi? Use Bluetooth PAN tethering with the `Url` mode, no extra code:

1. On the phone, enable Settings > Hotspot & tethering > Bluetooth tethering.
2. Pair the desktop with the phone and join its Bluetooth network (PAN). The desktop gets an ip such as `192.168.44.x`, which appears in Settings > Connection.
3. Use that ip as `FloconConnectionMode.Url(host = "<desktop PAN ip>")`, with the cleartext network config described above.

Throughput is about 1-2 Mbit/s. See [Bluetooth connection (exploration)](bluetooth-exploration.md) for the limits and the other options.

### Without any network: `FloconConnectionMode.Bluetooth` (experimental)

The app opens a Bluetooth RFCOMM server and waits for the desktop. Android only.

```kotlin
startFlocon(FloconContext(this)) {
    connectionMode = FloconConnectionMode.Bluetooth
    install(FloconNetwork)
}
```

1. The app must be granted `BLUETOOTH_CONNECT` at runtime (API 31+). The SDK declares the permission but cannot ask for it, it retries to connect until it is granted.
2. Pair the phone with the desktop, then start the app: it waits for the desktop.
3. Make the OS expose the Flocon service of the phone as a serial port (`b3c7e6a2-5f1d-4e8a-9a37-2f6d4c1e0a42`): see the hints in **Settings > Adb > Connection > Bluetooth**.
4. In that same section, refresh the ports and press **Connect** on the port of the phone.

The messages and the files go through the same Flocon server, only the carrier changes: see [the frame format](https://github.com/openflocon/Flocon/blob/main/protocol/transport-bluetooth.md). Only one app at a time, and large payloads are slow.

## Open questions / next steps

This mode is a prototype. Things to discuss before making it a first-class feature:

- **Discovery:** QR code on the desktop, or mDNS, instead of typing an ip in the code. The app side is ready for it: a scanner only has to call `Flocon.instance.setConnectionMode(...)`.
- **Pairing token:** the server is now reachable from the network, so the desktop should refuse apps that do not present a token.
- **TLS:** a scheme flag on `Url` (`wss` / `https`) with a certificate on the desktop.
- **Relay mode:** a relay between the app and the desktop for remote devices that cannot reach the desktop.
